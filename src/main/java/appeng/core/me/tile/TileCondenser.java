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

package appeng.core.me.tile;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidHandler;

import appeng.core.AppEngCore;
import appeng.core.api.config.CondenserOutput;
import appeng.core.api.config.Settings;
import appeng.core.api.implementations.items.IStorageComponent;
import appeng.core.api.material.Material;
import appeng.core.api.util.IConfigManager;
import appeng.core.api.util.IConfigurableObject;
import appeng.core.definitions.CoreMaterialDefinitions;
import appeng.core.lib.api.definitions.ApiMaterials;
import appeng.core.lib.tile.AEBaseInvTile;
import appeng.core.lib.tile.TileEvent;
import appeng.core.lib.tile.events.TileEventType;
import appeng.core.lib.tile.inventory.AppEngInternalInventory;
import appeng.core.lib.tile.inventory.InvOperation;
import appeng.core.lib.util.ConfigManager;
import appeng.core.lib.util.IConfigManagerHost;
import appeng.core.lib.util.Platform;


public class TileCondenser extends AEBaseInvTile implements IFluidHandler, IConfigManagerHost, IConfigurableObject
{

	private static final FluidTankInfo[] EMPTY = { new FluidTankInfo( null, 10 ) };
	private final int[] sides = { 0, 1 };
	private final AppEngInternalInventory inv = new AppEngInternalInventory( this, 3 );
	private final ConfigManager cm = new ConfigManager( this );

	private double storedPower = 0;

	public TileCondenser()
	{
		this.cm.registerSetting( Settings.CONDENSER_OUTPUT, CondenserOutput.TRASH );
	}

	@TileEvent( TileEventType.WORLD_NBT_WRITE )
	public void writeToNBT_TileCondenser( final NBTTagCompound data )
	{
		this.cm.writeToNBT( data );
		data.setDouble( "storedPower", this.getStoredPower() );
	}

	@TileEvent( TileEventType.WORLD_NBT_READ )
	public void readFromNBT_TileCondenser( final NBTTagCompound data )
	{
		this.cm.readFromNBT( data );
		this.setStoredPower( data.getDouble( "storedPower" ) );
	}

	public double getStorage()
	{
		final ItemStack is = this.inv.getStackInSlot( 2 );
		if( is != null )
		{
			if( is.getItem() instanceof IStorageComponent )
			{
				final IStorageComponent sc = (IStorageComponent) is.getItem();
				if( sc.isStorageComponent( is ) )
				{
					return sc.getBytes( is ) * 8;
				}
			}
		}
		return 0;
	}

	public void addPower( final double rawPower )
	{
		this.setStoredPower( this.getStoredPower() + rawPower );
		this.setStoredPower( Math.max( 0.0, Math.min( this.getStorage(), this.getStoredPower() ) ) );

		final double requiredPower = this.getRequiredPower();
		final ItemStack output = this.getOutput();
		while( requiredPower <= this.getStoredPower() && output != null && requiredPower > 0 )
		{
			if( this.canAddOutput( output ) )
			{
				this.setStoredPower( this.getStoredPower() - requiredPower );
				this.addOutput( output );
			}
			else
			{
				break;
			}
		}
	}

	private boolean canAddOutput( final ItemStack output )
	{
		final ItemStack outputStack = this.getStackInSlot( 1 );
		return outputStack == null || ( Platform.isSameItem( outputStack, output ) && outputStack.getCount() < outputStack.getMaxStackSize() );
	}

	/**
	 * make sure you validate with canAddOutput prior to this.
	 *
	 * @param output to be added output
	 */
	private void addOutput( final ItemStack output )
	{
		final ItemStack outputStack = this.getStackInSlot( 1 );
		if( outputStack == null )
		{
			this.setInventorySlotContents( 1, output.copy() );
		}
		else
		{
			outputStack.shrink(1);
			this.setInventorySlotContents( 1, outputStack );
		}
	}

	private ItemStack getOutput()
	{
		final CoreMaterialDefinitions materials = AppEngCore.INSTANCE.<Material, CoreMaterialDefinitions>definitions( Material.class );

		switch( (CondenserOutput) this.cm.getSetting( Settings.CONDENSER_OUTPUT ) )
		{
			case MATTER_BALLS:
				return (ItemStack) materials.matterBall().maybeStack( 1 ).orElse( null );

			case SINGULARITY:
				// TODO 1.11.2-CD:A - Singularity is a separate item now!
				return (ItemStack) materials.singularity().maybeStack( 1 ).orElse( null );

			case TRASH:
			default:
				return null;
		}
	}

	public double getRequiredPower()
	{
		return ( (CondenserOutput) this.cm.getSetting( Settings.CONDENSER_OUTPUT ) ).requiredPower;
	}

	@Override
	public IInventory getInternalInventory()
	{
		return this.inv;
	}

	@Override
	public void setInventorySlotContents( final int i, final ItemStack itemstack )
	{
		if( i == 0 )
		{
			if( itemstack != null )
			{
				this.addPower( itemstack.getCount() );
			}
		}
		else
		{
			this.inv.setInventorySlotContents( 1, itemstack );
		}
	}

	@Override
	public boolean isItemValidForSlot( final int i, final ItemStack itemstack )
	{
		return i == 0;
	}

	@Override
	public void onChangeInventory( final IInventory inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added )
	{
		if( slot == 0 )
		{
			final ItemStack is = inv.getStackInSlot( 0 );
			if( is != null )
			{
				this.addPower( is.getCount() );
				inv.setInventorySlotContents( 0, null );
			}
		}
	}

	@Override
	public boolean canInsertItem( final int slotIndex, final ItemStack insertingItem, final EnumFacing side )
	{
		return slotIndex == 0;
	}

	@Override
	public boolean canExtractItem( final int slotIndex, final ItemStack extractedItem, final EnumFacing side )
	{
		return slotIndex != 0;
	}

	@Override
	public int[] getAccessibleSlotsBySide( final EnumFacing side )
	{
		return this.sides;
	}

	@Override
	public int fill( final FluidStack resource, final boolean doFill )
	{
		if( doFill )
		{
			this.addPower( ( resource == null ? 0.0 : (double) resource.amount ) / 500.0 );
		}

		return resource == null ? 0 : resource.amount;
	}

	@Override
	public FluidStack drain( final FluidStack resource, final boolean doDrain )
	{
		return null;
	}

	@Override
	public FluidStack drain( final int maxDrain, final boolean doDrain )
	{
		return null;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo()
	{
		return EMPTY;
	}

	@Override
	public void updateSetting( final IConfigManager manager, final Enum settingName, final Enum newValue )
	{
		this.addPower( 0 );
	}

	@Override
	public IConfigManager getConfigManager()
	{
		return this.cm;
	}

	public double getStoredPower()
	{
		return this.storedPower;
	}

	private void setStoredPower( final double storedPower )
	{
		this.storedPower = storedPower;
	}
}
