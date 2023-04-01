package com.ultreon.devices.object.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.object.Game;

public class TileFlower extends Tile
{
	public TileFlower(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(PoseStack pose, Game game, int x, int y, Game.Layer layer)
	{
		RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 4, this.x * 16, this.y * 16, WIDTH, 8, 16, 16);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
