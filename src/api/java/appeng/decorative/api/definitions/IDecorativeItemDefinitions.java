
package appeng.decorative.api.definitions;


import net.minecraft.item.Item;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItemDefinition;


public interface IDecorativeItemDefinitions extends IDefinitions<Item, IItemDefinition<Item>>
{

	default IDefinition<Item> paintBall()
	{
		return get( "paint_ball" );
	}

}
