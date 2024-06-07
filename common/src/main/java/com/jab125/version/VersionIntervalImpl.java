/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jab125.version;

import java.util.*;

;

public record VersionIntervalImpl(Version min, boolean minInclusive, Version max,
								  boolean maxInclusive) implements VersionInterval {
	public VersionIntervalImpl(Version min, boolean minInclusive,
							   Version max, boolean maxInclusive) {
		this.min = min;
		this.minInclusive = min != null ? minInclusive : false;
		this.max = max;
		this.maxInclusive = max != null ? maxInclusive : false;

		assert min != null || !minInclusive;
		assert max != null || !maxInclusive;
		assert min == null || min instanceof SemanticVersion || minInclusive;
		assert max == null || max instanceof SemanticVersion || maxInclusive;
		assert min == null || max == null || min instanceof SemanticVersion && max instanceof SemanticVersion || min.equals(max);
	}

	@Override
	public boolean isSemantic() {
		return (min == null || min instanceof SemanticVersion)
				&& (max == null || max instanceof SemanticVersion);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VersionInterval) {
			VersionInterval o = (VersionInterval) obj;

			return Objects.equals(min, o.min()) && minInclusive == o.minInclusive()
					&& Objects.equals(max, o.max()) && maxInclusive == o.maxInclusive();
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (min == null) {
			if (max == null) {
				return "(-∞,∞)";
			} else {
				return String.format("(-∞,%s%c", max, maxInclusive ? ']' : ')');
			}
		} else if (max == null) {
			return String.format("%c%s,∞)", minInclusive ? '[' : '(', min);
		} else {
			return String.format("%c%s,%s%c", minInclusive ? '[' : '(', min, max, maxInclusive ? ']' : ')');
		}
	}

	public static VersionInterval and(VersionInterval a, VersionInterval b) {
		if (a == null || b == null) return null;

		if (!a.isSemantic() || !b.isSemantic()) {
			return andPlain(a, b);
		} else {
			return andSemantic(a, b);
		}
	}

	private static VersionInterval andPlain(VersionInterval a, VersionInterval b) {
		Version aMin = a.min();
		Version aMax = a.max();
		Version bMin = b.min();
		Version bMax = b.max();

		if (aMin != null) { // -> min must be aMin or invalid
			if (bMin != null && !aMin.equals(bMin) || bMax != null && !aMin.equals(bMax)) {
				return null;
			}

			if (aMax != null || bMax == null) {
				assert Objects.equals(aMax, bMax) || bMax == null;
				return a;
			} else {
				return new VersionIntervalImpl(aMin, true, bMax, b.maxInclusive());
			}
		} else if (aMax != null) { // -> min must be bMin, max must be aMax or invalid
			if (bMin != null && !aMax.equals(bMin) || bMax != null && !aMax.equals(bMax)) {
				return null;
			}

			if (bMin == null) {
				return a;
			} else if (bMax != null) {
				return b;
			} else {
				return new VersionIntervalImpl(bMin, true, aMax, true);
			}
		} else {
			return b;
		}
	}

	private static VersionInterval andSemantic(VersionInterval a, VersionInterval b) {
		int minCmp = compareMin(a, b);
		int maxCmp = compareMax(a, b);

		if (minCmp == 0) { // aMin == bMin
			if (maxCmp == 0) { // aMax == bMax -> a == b -> a/b
				return a;
			} else { // aMax != bMax -> a/b..min(a,b)
				return maxCmp < 0 ? a : b;
			}
		} else if (maxCmp == 0) { // aMax == bMax, aMin != bMin -> max(a,b)..a/b
			return minCmp < 0 ? b : a;
		} else if (minCmp < 0) { // aMin < bMin, aMax != bMax -> b..min(a,b)
			if (maxCmp > 0) return b; // a > b -> b

			SemanticVersion aMax = (SemanticVersion) a.max();
			SemanticVersion bMin = (SemanticVersion) b.min();
			int cmp = bMin.compareTo((Version) aMax);

			if (cmp < 0 || cmp == 0 && b.minInclusive() && a.maxInclusive()) {
				return new VersionIntervalImpl(bMin, b.minInclusive(), aMax, a.maxInclusive());
			} else {
				return null;
			}
		} else { // aMin > bMin, aMax != bMax -> a..min(a,b)
			if (maxCmp < 0) return a; // a < b -> a

			SemanticVersion aMin = (SemanticVersion) a.min();
			SemanticVersion bMax = (SemanticVersion) b.max();
			int cmp = aMin.compareTo((Version) bMax);

			if (cmp < 0 || cmp == 0 && a.minInclusive() && b.maxInclusive()) {
				return new VersionIntervalImpl(aMin, a.minInclusive(), bMax, b.maxInclusive());
			} else {
				return null;
			}
		}
	}

	public static List<VersionInterval> and(Collection<VersionInterval> a, Collection<VersionInterval> b) {
		if (a.isEmpty() || b.isEmpty()) return Collections.emptyList();

		if (a.size() == 1 && b.size() == 1) {
			VersionInterval merged = and(a.iterator().next(), b.iterator().next());
			return merged != null ? Collections.singletonList(merged) : Collections.emptyList();
		}

		// (a0 || a1 || a2) && (b0 || b1 || b2) == a0 && b0 && b1 && b2 || a1 && b0 && b1 && b2 || a2 && b0 && b1 && b2

		List<VersionInterval> allMerged = new ArrayList<>();

		for (VersionInterval intervalA : a) {
			for (VersionInterval intervalB : b) {
				VersionInterval merged = and(intervalA, intervalB);
				if (merged != null) allMerged.add(merged);
			}
		}

		if (allMerged.isEmpty()) return Collections.emptyList();
		if (allMerged.size() == 1) return allMerged;

		List<VersionInterval> ret = new ArrayList<>(allMerged.size());

		for (VersionInterval v : allMerged) {
			merge(v, ret);
		}

		return ret;
	}

	public static List<VersionInterval> or(Collection<VersionInterval> a, VersionInterval b) {
		if (a.isEmpty()) {
			if (b == null) {
				return Collections.emptyList();
			} else {
				return Collections.singletonList(b);
			}
		}

		List<VersionInterval> ret = new ArrayList<>(a.size() + 1);

		for (VersionInterval v : a) {
			merge(v, ret);
		}

		merge(b, ret);

		return ret;
	}

	private static void merge(VersionInterval a, List<VersionInterval> out) {
		if (a == null) return;

		if (out.isEmpty()) {
			out.add(a);
			return;
		}

		if (out.size() == 1) {
			VersionInterval e = out.get(0);

			if (e.min() == null && e.max() == null) {
				return;
			}
		}

		if (!a.isSemantic()) {
			mergePlain(a, out);
		} else {
			mergeSemantic(a, out);
		}
	}

	private static void mergePlain(VersionInterval a, List<VersionInterval> out) {
		Version aMin = a.min();
		Version aMax = a.max();
		Version v = aMin != null ? aMin : aMax;
		assert v != null;

		for (int i = 0; i < out.size(); i++) {
			VersionInterval c = out.get(i);

			if (v.equals(c.min())) {
				if (aMin == null) {
					assert aMax.equals(c.min());
					out.clear();
					out.add(INFINITE);
				} else if (aMax == null && c.max() != null) {
					out.set(i, a);
				}

				return;
			} else if (v.equals(c.max())) {
				assert c.min() == null;

				if (aMax == null) {
					assert aMin.equals(c.max());
					out.clear();
					out.add(INFINITE);
				}

				return;
			}
		}

		out.add(a);
	}

	private static void mergeSemantic(VersionInterval a, List<VersionInterval> out) {
		SemanticVersion aMin = (SemanticVersion) a.min();
		SemanticVersion aMax = (SemanticVersion) a.max();

		if (aMin == null && aMax == null) {
			out.clear();
			out.add(INFINITE);
			return;
		}

		for (int i = 0; i < out.size(); i++) {
			VersionInterval c = out.get(i);
			if (!c.isSemantic()) continue;

			SemanticVersion cMin = (SemanticVersion) c.min();
			SemanticVersion cMax = (SemanticVersion) c.max();
			int cmp;

			if (aMin == null) { // ..a..]
				if (cMax == null) { // ..a..] [..c..
					cmp = aMax.compareTo((Version) cMin);

					if (cmp < 0 || cmp == 0 && !a.maxInclusive() && !c.minInclusive()) { // ..a..]..[..c.. or ..a..)(..c..
						out.add(i, a);
					} else { // ..a..|..c.. or ..a.[..].c..
						out.clear();
						out.add(INFINITE);
					}

					return;
				} else { // ..a..] [..c..]
					cmp = compareMax(a, c);

					if (cmp >= 0) { // a encompasses c
						out.remove(i);
						i--;
					} else if (cMin == null) { // c encompasses a
						return;
					} else { // aMax < cMax
						cmp = aMax.compareTo((Version) cMin);

						if (cmp < 0 || cmp == 0 && !a.maxInclusive() && !c.minInclusive()) { // ..a..]..[..c..] or ..a..)(..c..]
							out.add(i, a);
						} else { // c extends a to the right
							out.set(i, new VersionIntervalImpl(null, false, cMax, c.maxInclusive()));
						}

						return;
					}
				}
			} else if (cMax == null) { // [..c..
				cmp = compareMin(a, c);

				if (cmp >= 0) { // c encompasses a
					// no-op
				} else if (aMax == null) { // a encompasses c
					while (out.size() > i) out.remove(i);
					out.add(a);
				} else { // aMin < cMin
					cmp = aMax.compareTo((Version) cMin);

					if (cmp < 0 || cmp == 0 && !a.maxInclusive() && !c.minInclusive()) { // [..a..]..[..c.. or [..a..)(..c..
						out.add(i, a);
					} else { // a extends c to the left
						out.set(i, new VersionIntervalImpl(aMin, a.minInclusive(), null, false));
					}
				}

				return;
			} else if ((cmp = aMin.compareTo((Version) cMax)) < 0 || cmp == 0 && (a.minInclusive() || c.maxInclusive())) {
				int cmp2;

				if (aMax == null || cMin == null || (cmp2 = aMax.compareTo((Version) cMin)) > 0 || cmp2 == 0 && (a.maxInclusive() || c.minInclusive())) {
					int cmpMin = compareMin(a, c);
					int cmpMax = compareMax(a, c);

					if (cmpMax <= 0) { // aMax <= cMax
						if (cmpMin < 0) { // aMin < cMin
							out.set(i, new VersionIntervalImpl(aMin, a.minInclusive(), cMax, c.maxInclusive()));
						}

						return;
					} else if (cmpMin > 0) { // aMin > cMin, aMax > cMax
						a = new VersionIntervalImpl(cMin, c.minInclusive(), aMax, a.maxInclusive());
					}

					out.remove(i);
					i--;
				} else {
					out.add(i, a);
					return;
				}
			}
		}

		out.add(a);
	}

	private static int compareMin(VersionInterval a, VersionInterval b) {
		SemanticVersion aMin = (SemanticVersion) a.min();
		SemanticVersion bMin = (SemanticVersion) b.min();
		int cmp;

		if (aMin == null) { // a <= b
			if (bMin == null) { // a == b == -inf
				return 0;
			} else { // bMin != null -> a < b
				return -1;
			}
		} else if (bMin == null || (cmp = aMin.compareTo((Version) bMin)) > 0 || cmp == 0 && !a.minInclusive() && b.minInclusive()) { // a > b
			return 1;
		} else if (cmp < 0 || a.minInclusive() && !b.minInclusive()) { // a < b
			return -1;
		} else { // cmp == 0 && a.minInclusive() == b.minInclusive() -> a == b
			return 0;
		}
	}

	private static int compareMax(VersionInterval a, VersionInterval b) {
		SemanticVersion aMax = (SemanticVersion) a.max();
		SemanticVersion bMax = (SemanticVersion) b.max();
		int cmp;

		if (aMax == null) { // a >= b
			if (bMax == null) { // a == b == inf
				return 0;
			} else { // bMax != null -> a > b
				return 1;
			}
		} else if (bMax == null || (cmp = aMax.compareTo((Version) bMax)) < 0 || cmp == 0 && !a.maxInclusive() && b.maxInclusive()) { // a < b
			return -1;
		} else if (cmp > 0 || a.maxInclusive() && !b.maxInclusive()) { // a > b
			return 1;
		} else { // cmp == 0 && a.maxInclusive() == b.maxInclusive() -> a == b
			return 0;
		}
	}

	public static List<VersionInterval> not(VersionInterval interval) {
		if (interval == null) { // () = empty interval -> infinite
			return Collections.singletonList(INFINITE);
		} else if (interval.min() == null) { // (-∞, = at least half-open towards min
			if (interval.max() == null) { // (-∞,∞) = infinite -> empty
				return Collections.emptyList();
			} else { // (-∞,x = left open towards min -> half open towards max
				return Collections.singletonList(new VersionIntervalImpl(interval.max(), !interval.maxInclusive(), null, false));
			}
		} else if (interval.max() == null) { // x,∞) = half open towards max -> half open towards min
			return Collections.singletonList(new VersionIntervalImpl(null, false, interval.min(), !interval.minInclusive()));
		} else if (interval.min().equals(interval.max()) && !interval.minInclusive() && !interval.maxInclusive()) { // (x,x) = effectively empty interval -> infinite
			return Collections.singletonList(INFINITE);
		} else { // closed interval -> 2 half open intervals on each side
			List<VersionInterval> ret = new ArrayList<>(2);
			ret.add(new VersionIntervalImpl(null, false, interval.min(), !interval.minInclusive()));
			ret.add(new VersionIntervalImpl(interval.max(), !interval.maxInclusive(), null, false));

			return ret;
		}
	}

	public static List<VersionInterval> not(Collection<VersionInterval> intervals) {
		if (intervals.isEmpty()) return Collections.singletonList(INFINITE);
		if (intervals.size() == 1) return not(intervals.iterator().next());

		// !(i0 || i1 || i2) == !i0 && !i1 && !i2

		List<VersionInterval> ret = null;

		for (VersionInterval v : intervals) {
			List<VersionInterval> inverted = not(v);

			if (ret == null) {
				ret = inverted;
			} else {
				ret = and(ret, inverted);
			}

			if (ret.isEmpty()) break;
		}

		return ret;
	}
}
