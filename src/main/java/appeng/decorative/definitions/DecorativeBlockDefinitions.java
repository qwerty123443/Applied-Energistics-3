
package appeng.decorative.definitions;


import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEngCore;
import appeng.core.block.BlockSkyStone;
import appeng.core.block.BlockSkyStone.SkystoneType;
import appeng.core.definitions.CoreBlockDefinitions;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.worldgen.block.BlockQuartz;
import appeng.decorative.AppEngDecorative;
import appeng.decorative.api.definitions.IDecorativeBlockDefinitions;
import appeng.decorative.block.BlockChiseledQuartz;
import appeng.decorative.block.BlockFluix;
import appeng.decorative.block.BlockPaint;
import appeng.decorative.block.BlockQuartzFixture;
import appeng.decorative.block.BlockQuartzPillar;
import appeng.decorative.block.BlockStairCommon;
import appeng.miscellaneous.AppEngMiscellaneous;


public class DecorativeBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IDecorativeBlockDefinitions
{

	private final IBlockDefinition quartzBlock;
	private final IBlockDefinition quartzPillar;
	private final IBlockDefinition chiseledQuartzBlock;
	private final IBlockDefinition quartzFixture;
	private final IBlockDefinition fluixBlock;
	private final IBlockDefinition skyStoneBlock;
	private final IBlockDefinition skyStoneBrick;
	private final IBlockDefinition skyStoneSmallBrick;
	private final IBlockDefinition skyStoneStairs;
	private final IBlockDefinition smoothSkyStoneStairs;
	private final IBlockDefinition skyStoneBrickStairs;
	private final IBlockDefinition skyStoneSmallBrickStairs;
	private final IBlockDefinition fluixStairs;
	private final IBlockDefinition quartzStairs;
	private final IBlockDefinition chiseledQuartzStairs;
	private final IBlockDefinition quartzPillarStairs;

	private final IBlockDefinition paint;

	public DecorativeBlockDefinitions( FeatureFactory registry )
	{
		FeatureFactory deco = registry.features( AEFeature.DecorativeQuartzBlocks );
		this.quartzBlock = deco.block( new ResourceLocation( AppEngDecorative.MODID, "quartz_block" ), new BlockQuartz() ).createDefaultItemBlock().build();
		this.quartzPillar = deco.block( new ResourceLocation( AppEngDecorative.MODID, "quartz_pillar" ), new BlockQuartzPillar() ).createDefaultItemBlock().build();
		this.chiseledQuartzBlock = deco.block( new ResourceLocation( AppEngDecorative.MODID, "chiseled_quartz_block" ), new BlockChiseledQuartz() ).createDefaultItemBlock().build();
		this.quartzFixture = registry.block( new ResourceLocation( AppEngDecorative.MODID, "quartz_fixture" ), new BlockQuartzFixture() ).features( AEFeature.DecorativeLights ).createDefaultItemBlock().build();

		this.fluixBlock = deco.block( new ResourceLocation( AppEngDecorative.MODID, "fluix_block" ), new BlockFluix() ).createDefaultItemBlock().build();
		this.skyStoneBlock = deco.block( new ResourceLocation( AppEngDecorative.MODID, "smooth_skystone" ), new BlockSkyStone( SkystoneType.BLOCK ) ).createDefaultItemBlock().build();
		this.skyStoneBrick = deco.block( new ResourceLocation( AppEngDecorative.MODID, "skystone_brick" ), new BlockSkyStone( SkystoneType.BRICK ) ).createDefaultItemBlock().build();
		this.skyStoneSmallBrick = deco.block( new ResourceLocation( AppEngDecorative.MODID, "skystone_small_brick" ), new BlockSkyStone( SkystoneType.SMALL_BRICK ) ).createDefaultItemBlock().build();

		this.skyStoneStairs = makeStairs( "skystone_stairs", registry, AppEngCore.INSTANCE.<Block, CoreBlockDefinitions>definitions( Block.class ).skyStone() );
		this.smoothSkyStoneStairs = makeStairs( "smooth_skystone_stairs", registry, this.skyStoneBlock );
		this.skyStoneBrickStairs = makeStairs( "skystone_brick_stairs", registry, this.skyStoneBrick );
		this.skyStoneSmallBrickStairs = makeStairs( "skystone_small_brick_stairs", registry, this.skyStoneSmallBrick );
		this.fluixStairs = makeStairs( "fluix_stairs", registry, this.fluixBlock );
		this.quartzStairs = makeStairs( "quartz_stairs", registry, this.quartzBlock );
		this.chiseledQuartzStairs = makeStairs( "chiseled_quartz_stairs", registry, this.chiseledQuartzBlock );
		this.quartzPillarStairs = makeStairs( "quartz_pillar_stairs", registry, this.quartzPillar );

		this.paint = registry.block( new ResourceLocation( AppEngMiscellaneous.MODID, "paint" ), new BlockPaint() ).features( AEFeature.PaintBalls ).createDefaultItemBlock().build();

		init();
	}

	private static IBlockDefinition makeStairs( String registryName, FeatureFactory registry, IBlockDefinition<Block> block )
	{
		return registry.block( new ResourceLocation( AppEngDecorative.MODID, registryName ), new BlockStairCommon( block.maybe().get(), block.identifier().getResourcePath() ) ).features( AEFeature.DecorativeQuartzBlocks ).createDefaultItemBlock().build();
	}

}
