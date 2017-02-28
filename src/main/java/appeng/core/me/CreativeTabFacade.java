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

package appeng.core.me;


import java.util.Optional;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import appeng.core.lib.AppEngApi;
import appeng.core.me.item.ItemFacade;


public final class CreativeTabFacade extends CreativeTabs
{

	public static CreativeTabFacade instance = null;

	public CreativeTabFacade()
	{
		super( "appliedenergistics2.facades" );
	}

	static void init()
	{
		instance = new CreativeTabFacade();
	}

	@Override
	public ItemStack getTabIconItem()
	{
		final Optional<Item> maybeFacade = AppEngApi.internalApi().definitions().items().facade().maybe();
		if( maybeFacade.isPresent() )
		{
			return ( (ItemFacade) maybeFacade.get() ).getCreativeTabIcon();
		}

		return new ItemStack( Blocks.PLANKS );
	}
}