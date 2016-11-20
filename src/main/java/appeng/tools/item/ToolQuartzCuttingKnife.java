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

package appeng.tools.item;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import appeng.core.api.implementations.guiobjects.IGuiItem;
import appeng.core.api.implementations.guiobjects.IGuiItemObject;
import appeng.core.api.util.AEPartLocation;
import appeng.core.lib.features.AEFeature;
import appeng.core.lib.item.AEBaseItem;
import appeng.core.lib.sync.GuiBridge;
import appeng.core.lib.util.Platform;
import appeng.core.me.item.QuartzKnifeObj;


public class ToolQuartzCuttingKnife extends AEBaseItem implements IGuiItem
{
	private final AEFeature type;

	public ToolQuartzCuttingKnife( final AEFeature type )
	{
		this.type = type;
		this.setMaxDamage( 50 );
		this.setMaxStackSize( 1 );
	}

	@Override
	public EnumActionResult onItemUse( final ItemStack stack, final EntityPlayer p, final World worldIn, final BlockPos pos, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ )
	{
		if( Platform.isServer() )
		{
			Platform.openGUI( p, null, AEPartLocation.INTERNAL, GuiBridge.GUI_QUARTZ_KNIFE );
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick( final ItemStack it, final World w, final EntityPlayer p, final EnumHand hand )
	{
		if( Platform.isServer() )
		{
			Platform.openGUI( p, null, AEPartLocation.INTERNAL, GuiBridge.GUI_QUARTZ_KNIFE );
		}
		p.swingArm( hand );
		return new ActionResult<ItemStack>( EnumActionResult.SUCCESS, it );
	}

	@Override
	public boolean getIsRepairable( final ItemStack a, final ItemStack b )
	{
		return Platform.canRepair( this.type, a, b );
	}

	@Override
	public boolean isRepairable()
	{
		return false;
	}

	@Override
	public ItemStack getContainerItem( final ItemStack itemStack )
	{
		itemStack.setItemDamage( itemStack.getItemDamage() + 1 );
		return itemStack;
	}

	@Override
	public boolean hasContainerItem( final ItemStack stack )
	{
		return true;
	}

	@Override
	public IGuiItemObject getGuiObject( final ItemStack is, final World world, final BlockPos pos )
	{
		return new QuartzKnifeObj( is );
	}
}
