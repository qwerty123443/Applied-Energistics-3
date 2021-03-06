
package appeng.tools.definitions;


import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.tools.api.definitions.IToolsMaterialDefinitions;


public class ToolsMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements IToolsMaterialDefinitions
{

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	private final IMaterialDefinition matterBall;

	public ToolsMaterialDefinitions( FeatureFactory registry )
	{
		matterBall = registry.material( "matter_ball", new Material() ).model( new ModelResourceLocation( new ResourceLocation( AppEng.MODID, MATERIALSMODELSLOCATION + "matter_ball" ), MATERIALSMODELSVARIANT ) ).build();

		init();
	}

}
