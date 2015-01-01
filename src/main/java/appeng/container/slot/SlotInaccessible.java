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

package appeng.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotInaccessible extends AppEngSlot
{

	public SlotInaccessible(IInventory i, int slotIdx, int x, int y) {
		super( i, slotIdx, x, y );
	}

	ItemStack dspStack = null;

	@Override
	public ItemStack getDisplayStack()
	{
		if ( this.dspStack == null )
		{
			ItemStack dsp = super.getDisplayStack();
			if ( dsp != null )
				this.dspStack = dsp.copy();
		}
		return this.dspStack;
	}

	@Override
	public void onSlotChanged()
	{
		super.onSlotChanged();
		this.dspStack = null;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
	{
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack i)
	{
		return false;
	}

}