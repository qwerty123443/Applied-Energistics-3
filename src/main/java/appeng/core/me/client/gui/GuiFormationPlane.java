/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.core.me.client.gui;


import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import appeng.core.api.config.FuzzyMode;
import appeng.core.api.config.Settings;
import appeng.core.api.config.YesNo;
import appeng.core.lib.client.gui.GuiUpgradeable;
import appeng.core.lib.client.gui.widgets.GuiImgButton;
import appeng.core.lib.client.gui.widgets.GuiTabButton;
import appeng.core.lib.localization.GuiText;
import appeng.core.lib.sync.GuiBridge;
import appeng.core.lib.sync.network.NetworkHandler;
import appeng.core.lib.sync.packets.PacketConfigButton;
import appeng.core.lib.sync.packets.PacketSwitchGuis;
import appeng.core.me.container.ContainerFormationPlane;
import appeng.core.me.part.automation.PartFormationPlane;


public class GuiFormationPlane extends GuiUpgradeable
{

	private GuiTabButton priority;
	private GuiImgButton placeMode;

	public GuiFormationPlane( final InventoryPlayer inventoryPlayer, final PartFormationPlane te )
	{
		super( new ContainerFormationPlane( inventoryPlayer, te ) );
		this.ySize = 251;
	}

	@Override
	protected void addButtons()
	{
		this.placeMode = new GuiImgButton( this.guiLeft - 18, this.guiTop + 28, Settings.PLACE_BLOCK, YesNo.YES );
		this.fuzzyMode = new GuiImgButton( this.guiLeft - 18, this.guiTop + 48, Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL );

		this.buttonList.add( this.priority = new GuiTabButton( this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender ) );

		this.buttonList.add( this.placeMode );
		this.buttonList.add( this.fuzzyMode );
	}

	@Override
	public void drawFG( final int offsetX, final int offsetY, final int mouseX, final int mouseY )
	{
		this.fontRendererObj.drawString( this.getGuiDisplayName( GuiText.FormationPlane.getLocal() ), 8, 6, 4210752 );
		this.fontRendererObj.drawString( GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752 );

		if( this.fuzzyMode != null )
		{
			this.fuzzyMode.set( this.cvb.getFuzzyMode() );
		}

		if( this.placeMode != null )
		{
			this.placeMode.set( ( (ContainerFormationPlane) this.cvb ).getPlaceMode() );
		}
	}

	@Override
	protected String getBackground()
	{
		return "guis/storagebus.png";
	}

	@Override
	protected void actionPerformed( final GuiButton btn ) throws IOException
	{
		super.actionPerformed( btn );

		final boolean backwards = Mouse.isButtonDown( 1 );

		if( btn == this.priority )
		{
			NetworkHandler.instance.sendToServer( new PacketSwitchGuis( GuiBridge.GUI_PRIORITY ) );
		}
		else if( btn == this.placeMode )
		{
			NetworkHandler.instance.sendToServer( new PacketConfigButton( this.placeMode.getSetting(), backwards ) );
		}
	}
}
