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

package appeng.core.tile;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import appeng.core.api.features.IGrinderEntry;
import appeng.core.api.implementations.tiles.ICrankable;
import appeng.core.lib.AppEngApi;
import appeng.core.lib.tile.AEBaseInvTile;
import appeng.core.lib.tile.inventory.AppEngInternalInventory;
import appeng.core.lib.tile.inventory.InvOperation;
import appeng.core.lib.util.InventoryAdaptor;
import appeng.core.lib.util.Platform;
import appeng.core.lib.util.inv.WrapperInventoryRange;


public class TileGrinder extends AEBaseInvTile implements ICrankable
{

	private final int[] inputs = { 0, 1, 2 };
	private final int[] sides = { 0, 1, 2, 3, 4, 5 };
	private final AppEngInternalInventory inv = new AppEngInternalInventory( this, 7 );
	private int points;

	@Override
	public void setOrientation( final EnumFacing inForward, final EnumFacing inUp )
	{
		super.setOrientation( inForward, inUp );
		final IBlockState state = this.world.getBlockState( this.pos );
		this.getBlockType().neighborChanged( state, this.world, this.pos, state.getBlock(), this.pos );
	}

	@Override
	public IInventory getInternalInventory()
	{
		return this.inv;
	}

	@Override
	public void onChangeInventory( final IInventory inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added )
	{

	}

	@Override
	public boolean canInsertItem( final int slotIndex, final ItemStack insertingItem, final EnumFacing side )
	{
		if( AppEngApi.internalApi().registries().grinder().getRecipeForInput( insertingItem ) == null )
		{
			return false;
		}

		return slotIndex >= 0 && slotIndex <= 2;
	}

	@Override
	public boolean canExtractItem( final int slotIndex, final ItemStack extractedItem, final EnumFacing side )
	{
		return slotIndex >= 3 && slotIndex <= 5;
	}

	@Override
	public int[] getAccessibleSlotsBySide( final EnumFacing side )
	{
		return this.sides;
	}

	@Override
	public boolean canTurn()
	{
		if( Platform.isClient() )
		{
			return false;
		}

		if( null == this.getStackInSlot( 6 ) ) // Add if there isn't one...
		{
			final IInventory src = new WrapperInventoryRange( this, this.inputs, true );
			for( int x = 0; x < src.getSizeInventory(); x++ )
			{
				ItemStack item = src.getStackInSlot( x );
				if( item == null )
				{
					continue;
				}

				final IGrinderEntry r = AppEngApi.internalApi().registries().grinder().getRecipeForInput( item );
				if( r != null )
				{
					if( item.getCount() >= r.getInput().getCount() )
					{
						item.shrink(r.getInput().getCount());
						final ItemStack ais = item.copy();
						ais.setCount(r.getInput().getCount());

						if( item.getCount() <= 0 )
						{
							item = null;
						}

						src.setInventorySlotContents( x, item );
						this.setInventorySlotContents( 6, ais );
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public void applyTurn()
	{
		if( Platform.isClient() )
		{
			return;
		}

		this.points++;

		final ItemStack processing = this.getStackInSlot( 6 );
		final IGrinderEntry r = AppEngApi.internalApi().registries().grinder().getRecipeForInput( processing );
		if( r != null )
		{
			if( r.getEnergyCost() > this.points )
			{
				return;
			}

			this.points = 0;
			final InventoryAdaptor sia = InventoryAdaptor.getAdaptor( new WrapperInventoryRange( this, 3, 3, true ), EnumFacing.EAST );

			this.addItem( sia, r.getOutput() );

			float chance = ( Platform.getRandomInt() % 2000 ) / 2000.0f;
			if( chance <= r.getOptionalChance() )
			{
				this.addItem( sia, r.getOptionalOutput() );
			}

			chance = ( Platform.getRandomInt() % 2000 ) / 2000.0f;
			if( chance <= r.getSecondOptionalChance() )
			{
				this.addItem( sia, r.getSecondOptionalOutput() );
			}

			this.setInventorySlotContents( 6, null );
		}
	}

	private void addItem( final InventoryAdaptor sia, final ItemStack output )
	{
		if( output == null )
		{
			return;
		}

		final ItemStack notAdded = sia.addItems( output );
		if( notAdded != null )
		{
			final List<ItemStack> out = new ArrayList<ItemStack>();
			out.add( notAdded );

			Platform.spawnDrops( this.world, this.pos.offset( this.getForward() ), out );
		}
	}

	@Override
	public boolean canCrankAttach( final EnumFacing directionToCrank )
	{
		return this.getUp() == directionToCrank;
	}
}
