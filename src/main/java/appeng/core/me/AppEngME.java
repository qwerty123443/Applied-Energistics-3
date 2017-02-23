
package appeng.core.me;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.ClearCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.SubstitutionCallback;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.api.module.ModuleIMCMessageEvent;
import appeng.core.AppEng;
import appeng.core.lib.AEConfig;
import appeng.core.lib.FacadeConfig;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.api.IME;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.api.parts.IPart;
import appeng.core.me.bootstrap.MEFeatureFactory;
import appeng.core.me.definitions.MEBlockDefinitions;
import appeng.core.me.definitions.MEItemDefinitions;
import appeng.core.me.definitions.MEPartDefinitions;
import appeng.core.me.definitions.METileDefinitions;


@Module( IME.NAME )
public class AppEngME implements IME
{

	@Module.Instance( NAME )
	public static final AppEngME INSTANCE = null;

	private static final ResourceLocation CLASS2PARTMAP = new ResourceLocation( AppEng.MODID, "class-partrege_map" );

	private FMLControlledNamespacedRegistry<PartRegistryEntry<?>> partRegistry;

	private MEFeatureFactory registry;

	private MEItemDefinitions itemDefinitions;
	private MEBlockDefinitions blockDefinitions;
	private METileDefinitions tileDefinitions;
	private MEPartDefinitions partDefinitions;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions( Class<T> clas )
	{
		if( clas == Item.class )
		{
			return (D) itemDefinitions;
		}
		if( clas == Block.class )
		{
			return (D) blockDefinitions;
		}
		if( clas == TileEntity.class )
		{
			return (D) tileDefinitions;
		}
		if( clas == PartRegistryEntry.class )
		{
			return (D) partDefinitions;
		}
		return null;
	}

	public FMLControlledNamespacedRegistry<PartRegistryEntry<?>> getPartRegistry()
	{
		return partRegistry;
	}

	private Map<Class<?>, ResourceLocation> getClass2PartMap( Map<ResourceLocation, ?> slaveset )
	{
		return (Map<Class<?>, ResourceLocation>) slaveset.get( CLASS2PARTMAP );
	}

	public ResourceLocation getRegistryName( Class<? extends IPart> clas )
	{
		return (ResourceLocation) partRegistry.getSlaveMap( CLASS2PARTMAP, Map.class ).get( clas );
	}

	@ModuleEventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		// @formatter:off
		partRegistry = (FMLControlledNamespacedRegistry<PartRegistryEntry<?>>) new RegistryBuilder().setName( new ResourceLocation( AppEng.MODID, "parts" ) ).setType( PartRegistryEntry.class )
				.add( (CreateCallback<PartRegistryEntry<?>>) ( ( Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries ) -> ( (Map) slaveset ).put( CLASS2PARTMAP, new HashMap() ) ) )
				.add( (AddCallback<PartRegistryEntry<?>>) ( ( PartRegistryEntry<?> part, int id, Map<ResourceLocation, ?> slaveset ) -> getClass2PartMap( slaveset ).put( part.getPartClass(), part.getRegistryName() ) ) )
				.add( (ClearCallback<PartRegistryEntry<?>>) ( ( IForgeRegistry<PartRegistryEntry<?>> is, Map<ResourceLocation, ?> slaveset ) -> getClass2PartMap( slaveset ).clear() ) )
				.add( (SubstitutionCallback<PartRegistryEntry<?>>) ( ( Map<ResourceLocation, ?> slaveset, PartRegistryEntry<?> original, PartRegistryEntry<?> replacement, ResourceLocation name ) -> {
					getClass2PartMap( slaveset ).remove( original.getClass() );
					getClass2PartMap( slaveset ).put( replacement.getClass(), name );
				} ) ).create();
		// @formatter:on

		registry = new MEFeatureFactory();
		this.blockDefinitions = new MEBlockDefinitions( registry );
		this.itemDefinitions = new MEItemDefinitions( registry );
		this.tileDefinitions = new METileDefinitions( registry );
		this.partDefinitions = new MEPartDefinitions<>( registry );
		registry.preInit( event );

		FacadeConfig.instance = new FacadeConfig( new File( AppEng.instance().getConfigDirectory(), "Facades.cfg" ) );
		if( AEConfig.instance.isFeatureEnabled( AEFeature.Facades ) )
		{
			CreativeTabFacade.init();
		}
	}

	@ModuleEventHandler
	public void init( FMLInitializationEvent event )
	{
		registry.init( event );
	}

	@ModuleEventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		registry.postInit( event );
	}

	@ModuleEventHandler
	public void handleIMCEvent( ModuleIMCMessageEvent event )
	{

	}

	@ModuleEventHandler
	public void serverAboutToStart( FMLServerAboutToStartEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStarting( FMLServerStartingEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStopping( FMLServerStoppingEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStopped( FMLServerStoppedEvent event )
	{

	}

}
