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

package appeng.core.crafting.container;


import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import appeng.core.api.config.RedstoneMode;
import appeng.core.api.config.SecurityPermissions;
import appeng.core.api.config.Settings;
import appeng.core.crafting.item.ItemPattern;
import appeng.core.crafting.tile.TileMolecularAssembler;
import appeng.core.lib.container.ContainerUpgradeable;
import appeng.core.lib.container.guisync.GuiSync;
import appeng.core.lib.container.interfaces.IProgressProvider;
import appeng.core.lib.container.slot.SlotMACPattern;
import appeng.core.lib.container.slot.SlotOutput;
import appeng.core.lib.container.slot.SlotRestrictedInput;
import appeng.core.lib.util.Platform;
import appeng.core.me.api.networking.crafting.ICraftingPatternDetails;


public class ContainerMAC extends ContainerUpgradeable implements IProgressProvider
{

	private static final int MAX_CRAFT_PROGRESS = 100;
	private final TileMolecularAssembler tma;
	@GuiSync( 4 )
	public int craftProgress = 0;

	public ContainerMAC( final InventoryPlayer ip, final TileMolecularAssembler te )
	{
		super( ip, te );
		this.tma = te;
	}

	public boolean isValidItemForSlot( final int slotIndex, final ItemStack i )
	{
		final IInventory mac = this.getUpgradeable().getInventoryByName( "mac" );

		final ItemStack is = mac.getStackInSlot( 10 );
		if( is == null )
		{
			return false;
		}

		if( is.getItem() instanceof ItemPattern )
		{
			final World w = this.getTileEntity().getWorld();
			final ItemPattern iep = (ItemPattern) is.getItem();
			final ICraftingPatternDetails ph = iep.getPatternForItem( is, w );
			if( ph.isCraftable() )
			{
				return ph.isValidItemForSlot( slotIndex, i, w );
			}
		}

		return false;
	}

	@Override
	protected int getHeight()
	{
		return 197;
	}

	@Override
	protected void setupConfig()
	{
		int offX = 29;
		int offY = 30;

		final IInventory mac = this.getUpgradeable().getInventoryByName( "mac" );

		for( int y = 0; y < 3; y++ )
		{
			for( int x = 0; x < 3; x++ )
			{
				final SlotMACPattern s = new SlotMACPattern( this, mac, x + y * 3, offX + x * 18, offY + y * 18 );
				this.addSlotToContainer( s );
			}
		}

		offX = 126;
		offY = 16;

		this.addSlotToContainer( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.ENCODED_CRAFTING_PATTERN, mac, 10, offX, offY, this.getInventoryPlayer() ) );
		this.addSlotToContainer( new SlotOutput( mac, 9, offX, offY + 32, -1 ) );

		offX = 122;
		offY = 17;

		final IInventory upgrades = this.getUpgradeable().getInventoryByName( "upgrades" );
		this.addSlotToContainer( ( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.UPGRADES, upgrades, 0, 187, 8, this.getInventoryPlayer() ) ).setNotDraggable() );
		this.addSlotToContainer( ( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.UPGRADES, upgrades, 1, 187, 8 + 18, this.getInventoryPlayer() ) ).setNotDraggable() );
		this.addSlotToContainer( ( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.UPGRADES, upgrades, 2, 187, 8 + 18 * 2, this.getInventoryPlayer() ) ).setNotDraggable() );
		this.addSlotToContainer( ( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.UPGRADES, upgrades, 3, 187, 8 + 18 * 3, this.getInventoryPlayer() ) ).setNotDraggable() );
		this.addSlotToContainer( ( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.UPGRADES, upgrades, 4, 187, 8 + 18 * 4, this.getInventoryPlayer() ) ).setNotDraggable() );
	}

	@Override
	protected boolean supportCapacity()
	{
		return false;
	}

	@Override
	public int availableUpgrades()
	{
		return 5;
	}

	@Override
	public void detectAndSendChanges()
	{
		this.verifyPermissions( SecurityPermissions.BUILD, false );

		if( Platform.isServer() )
		{
			this.setRedStoneMode( (RedstoneMode) this.getUpgradeable().getConfigManager().getSetting( Settings.REDSTONE_CONTROLLED ) );
		}

		this.craftProgress = this.tma.getCraftingProgress();

		this.standardDetectAndSendChanges();
	}

	@Override
	public int getCurrentProgress()
	{
		return this.craftProgress;
	}

	@Override
	public int getMaxProgress()
	{
		return MAX_CRAFT_PROGRESS;
	}
}
