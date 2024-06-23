package com.ultreon.devices.object.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.object.Game;

public class TileCactus extends Tile
{
	public TileCactus(int id)
	{
		super(id, 3, 2);
	}

	@Override
	public void render(MatrixStack pose, Game game, int x, int y, Game.Layer layer)
	{
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Game.Layer.FOREGROUND)
		{
			RenderUtil.drawRectWithTexture(pose,game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH + 0.5, game.yPosition + y * HEIGHT - 5.5, layer.zLevel, (this.x + 1) * 16 + 1, this.y * 16 + 1, WIDTH - 1, HEIGHT - 1, 14, 14);
		}

		RenderSystem.blendColor(0.6f, 0.6f, 0.6f, 1f);
		RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 0.5, layer.zLevel, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
		RenderSystem.blendColor(1f, 1f, 1f, 1f);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
