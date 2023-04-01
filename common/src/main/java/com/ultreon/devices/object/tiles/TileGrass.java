package com.ultreon.devices.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.object.Game;

public class TileGrass extends Tile
{
	public TileGrass(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(PoseStack pose, Game game, int x, int y, Game.Layer layer)
	{
		super.render(pose, game, x, y, layer);
		if(!game.isFullTile(layer, x, y + 1))
		{
			RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT + 6, dirt.x * 16, dirt.y * 16, WIDTH, HEIGHT, 16, 16);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
	}

	@Override
	public void renderForeground(PoseStack pose, Game game, int x, int y, Game.Layer layer)
	{
		super.renderForeground(pose, game, x, y, layer);

		if(game.getTile(layer, x, y - 1) == water)
		{
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 1, 16, 16, 8, 1, 16, 2);
		}

		if(game.getTile(layer, x, y + 1) == water)
		{
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT + 6, 16, 18, 8, 3, 16, 6);
		}

		if(game.getTile(layer, x - 1, y) == water)
		{
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH - 1, game.yPosition + y * HEIGHT, 0, 16, 1, 6, 2, 12);
		}

		if(game.getTile(layer, x + 1, y) == water)
		{
			RenderUtil.drawRectWithTexture(pose, game.xPosition + x * WIDTH + 8, game.yPosition + y * HEIGHT, 2, 16, 1, 6, 2, 12);
		}
	}

	@Override
	public boolean isSlow()
	{
		return true;
	}

}
