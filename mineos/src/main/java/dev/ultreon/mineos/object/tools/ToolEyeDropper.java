package dev.ultreon.mineos.object.tools;

import dev.ultreon.mineos.object.Canvas;
import dev.ultreon.mineos.object.Tool;

public class ToolEyeDropper extends Tool {

	@Override
	public void handleClick(Canvas canvas, int x, int y) {
		canvas.setColor(canvas.getPixel(x, y));
	}

	@Override
	public void handleRelease(Canvas canvas, int x, int y) {
	}

	@Override
	public void handleDrag(Canvas canvas, int x, int y) {
	}

}
