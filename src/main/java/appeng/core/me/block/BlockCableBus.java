/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
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

package appeng.core.me.block;


import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import appeng.core.api.util.AEColor;
import appeng.core.lib.AEConfig;
import appeng.core.lib.block.AEBaseTileBlock;
import appeng.core.lib.features.AEFeature;
import appeng.core.lib.helpers.AEGlassMaterial;
import appeng.core.lib.tile.AEBaseTile;
import appeng.core.lib.util.Platform;
import appeng.core.me.api.parts.IPartHost;
import appeng.core.me.api.parts.PartItemStack;
import appeng.core.me.api.parts.SelectedPart;
import appeng.core.me.part.ICableBusContainer;
import appeng.core.me.part.NullCableBusContainer;
import appeng.core.me.tile.TileCableBus;


public class BlockCableBus extends AEBaseTileBlock
{

	private static final ICableBusContainer NULL_CABLE_BUS = new NullCableBusContainer();

	public BlockCableBus()
	{
		super( AEGlassMaterial.INSTANCE );
		this.setLightOpacity( 0 );
		this.setFullSize( this.setOpaque( false ) );

		// this will actually be overwritten later through setupTile and the
		// combined layers
		this.setTileEntity( TileCableBus.class );
	}

	public static final CableBusContainerUnlistedProperty cableBus = new CableBusContainerUnlistedProperty();

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState( this, new IProperty[0], new IUnlistedProperty[] { FORWARD, UP, cableBus } );
	}

	@Override
	public IBlockState getExtendedState( IBlockState state, IBlockAccess world, BlockPos pos )
	{
		return ( (IExtendedBlockState) super.getExtendedState( state, world, pos ) ).withProperty( cableBus, ( (TileCableBus) world.getTileEntity( pos ) ).getCableBus() );
	}

	@Override
	public void randomDisplayTick( final IBlockState state, final World worldIn, final BlockPos pos, final Random rand )
	{
		this.cb( worldIn, pos ).randomDisplayTick( worldIn, pos, rand );
	}

	@Override
	public void onNeighborChange( final IBlockAccess w, final BlockPos pos, final BlockPos neighbor )
	{
		this.cb( w, pos ).onNeighborChanged();
	}

	@Override
	public Item getItemDropped( final IBlockState state, final Random rand, final int fortune )
	{
		return null;
	}

	@Override
	public int getWeakPower( final IBlockState state, final IBlockAccess w, final BlockPos pos, final EnumFacing side )
	{
		return this.cb( w, pos ).isProvidingWeakPower( side.getOpposite() ); // TODO Pre-1.8 - IS OPPOSITE!?
	}

	@Override
	public boolean canProvidePower( final IBlockState state )
	{
		return true;
	}

	@Override
	public void onEntityCollidedWithBlock( final World w, final BlockPos pos, final IBlockState state, final Entity entityIn )
	{
		this.cb( w, pos ).onEntityCollision( entityIn );
	}

	@Override
	public int getStrongPower( final IBlockState state, final IBlockAccess w, final BlockPos pos, final EnumFacing side )
	{
		return this.cb( w, pos ).isProvidingStrongPower( side.getOpposite() ); // TODO Pre-1.8 - IS OPPOSITE!?
	}

	@Override
	public int getLightValue( final IBlockState state, final IBlockAccess world, final BlockPos pos )
	{
		if( state != null && state.getBlock() != this )
		{
			return state.getBlock().getLightValue( state, world, pos );
		}
		if( state == null )
		{
			return 0;
		}
		return this.cb( world, pos ).getLightValue();
	}

	@Override
	public boolean isLadder( final IBlockState state, final IBlockAccess world, final BlockPos pos, final EntityLivingBase entity )
	{
		return this.cb( world, pos ).isLadder( entity );
	}

	@Override
	public boolean isSideSolid( final IBlockState state, final IBlockAccess w, final BlockPos pos, final EnumFacing side )
	{
		return this.cb( w, pos ).isSolidOnSide( side );
	}

	@Override
	public boolean isReplaceable( final IBlockAccess w, final BlockPos pos )
	{
		return this.cb( w, pos ).isEmpty();
	}

	@Override
	public boolean removedByPlayer( final IBlockState state, final World world, final BlockPos pos, final EntityPlayer player, final boolean willHarvest )
	{
		if( player.capabilities.isCreativeMode )
		{
			final AEBaseTile tile = this.getTileEntity( world, pos );
			if( tile != null )
			{
				tile.disableDrops();
			}
			// maybe ray trace?
		}
		return super.removedByPlayer( state, world, pos, player, willHarvest );
	}

	@Override
	public boolean canConnectRedstone( final IBlockState state, final IBlockAccess w, final BlockPos pos, EnumFacing side )
	{
		if( side == null )
		{
			side = EnumFacing.UP;
		}

		return this.cb( w, pos ).canConnectRedstone( EnumSet.of( side ) );
	}

	@Override
	public boolean canRenderInLayer( final IBlockState state, final BlockRenderLayer layer )
	{
		if( AEConfig.instance.isFeatureEnabled( AEFeature.AlphaPass ) )
		{
			return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
		}

		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public ItemStack getPickBlock( final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player )
	{
		final Vec3d v3 = target.hitVec.subtract( pos.getX(), pos.getY(), pos.getZ() );
		final SelectedPart sp = this.cb( world, pos ).selectPart( v3 );

		if( sp.part != null )
		{
			return sp.part.getItemStack( PartItemStack.Pick );
		}
		else if( sp.facade != null )
		{
			return sp.facade.getItemStack();
		}

		return null;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public boolean addHitEffects( final IBlockState state, final World world, final RayTraceResult target, final ParticleManager effectRenderer )
	{
		final Object object = this.cb( world, target.getBlockPos() );
		if( object instanceof IPartHost )
		{
			final IPartHost host = (IPartHost) object;

			// TODO Pre-1.8 - HIT EFFECTS
			/*
			 * for( AEPartLocation side : AEPartLocation.values() ) { IPart p =
			 * host.getPart( side ); TextureAtlasSprite ico = this.getIcon( p );
			 * if( ico == null ) { continue; } byte b0 = (byte) (
			 * Platform.getRandomInt() % 2 == 0 ? 1 : 0 ); for( int i1 = 0; i1 <
			 * b0; ++i1 ) { for( int j1 = 0; j1 < b0; ++j1 ) { for( int k1 = 0;
			 * k1 < b0; ++k1 ) { double d0 = target.blockX + ( i1 + 0.5D ) / b0;
			 * double d1 = target.blockY + ( j1 + 0.5D ) / b0; double d2 =
			 * target.blockZ + ( k1 + 0.5D ) / b0; double dd0 =
			 * target.hitVec.xCoord; double dd1 = target.hitVec.yCoord; double
			 * dd2 = target.hitVec.zCoord; EntityDiggingFX fx = ( new
			 * EntityDiggingFX( world, dd0, dd1, dd2, d0 - target.blockX - 0.5D,
			 * d1 - target.blockY - 0.5D, d2 - target.blockZ - 0.5D, this, 0 )
			 * ).applyColourMultiplier( target.blockX, target.blockY,
			 * target.blockZ ); fx.setParticleIcon( ico );
			 * effectRenderer.addEffect( fx ); } } } }
			 */
		}

		return true;
	}

	@Override
	public boolean addDestroyEffects( final World world, final BlockPos pos, final ParticleManager effectRenderer )
	{
		final Object object = this.cb( world, pos );
		if( object instanceof IPartHost )
		{
			final IPartHost host = (IPartHost) object;

			// TODO Pre-1.8 - DESTROY EFFECTS
			/*
			 * for( AEPartLocation side : AEPartLocation.values() ) { IPart p =
			 * host.getPart( side ); TextureAtlasSprite ico = this.getIcon( p );
			 * if( ico == null ) { continue; } byte b0 = 3; for( int i1 = 0; i1
			 * < b0; ++i1 ) { for( int j1 = 0; j1 < b0; ++j1 ) { for( int k1 =
			 * 0; k1 < b0; ++k1 ) { double d0 = x + ( i1 + 0.5D ) / b0; double
			 * d1 = y + ( j1 + 0.5D ) / b0; double d2 = z + ( k1 + 0.5D ) / b0;
			 * EntityDiggingFX fx = ( new EntityDiggingFX( world, d0, d1, d2, d0
			 * - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D, this, meta )
			 * ).applyColourMultiplier( x, y, z ); fx.setParticleIcon( ico );
			 * effectRenderer.addEffect( fx ); } } } }
			 */
		}

		return true;
	}

	@Override
	public void neighborChanged( final IBlockState state, final World w, final BlockPos pos, final Block neighborBlock, final BlockPos updated )
	{
		if( Platform.isServer() )
		{
			this.cb( w, pos ).onNeighborChanged();
		}
	}

	private ICableBusContainer cb( final IBlockAccess w, final BlockPos pos )
	{
		final TileEntity te = w.getTileEntity( pos );
		ICableBusContainer out = null;

		if( te instanceof TileCableBus )
		{
			out = ( (TileCableBus) te ).getCableBus();
		}

		return out == null ? NULL_CABLE_BUS : out;
	}

	@Override
	public boolean onActivated( final World w, final BlockPos pos, final EntityPlayer player, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ )
	{
		return this.cb( w, pos ).activate( player, hand, new Vec3d( hitX, hitY, hitZ ) );
	}

	@Override
	public boolean recolorBlock( final World world, final BlockPos pos, final EnumFacing side, final EnumDyeColor color )
	{
		return this.recolorBlock( world, pos, side, color, null );
	}

	public boolean recolorBlock( final World world, final BlockPos pos, final EnumFacing side, final EnumDyeColor color, final EntityPlayer who )
	{
		try
		{
			return this.cb( world, pos ).recolourBlock( side, AEColor.values()[color.ordinal()], who );
		}
		catch( final Throwable ignored )
		{
		}
		return false;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void getCheckedSubBlocks( final Item item, final CreativeTabs tabs, final NonNullList<ItemStack> itemStacks )
	{
		// do nothing
	}

}
