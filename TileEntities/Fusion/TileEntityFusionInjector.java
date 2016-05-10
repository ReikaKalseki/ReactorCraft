/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.TileEntities.Fusion;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.ToggleTile;
import Reika.ReactorCraft.Auxiliary.FusionReactorToroidPart;
import Reika.ReactorCraft.Auxiliary.MultiBlockTile;
import Reika.ReactorCraft.Base.BlockMultiBlock;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Entities.EntityPlasma;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityMagneticPipe;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFusionInjector extends TileEntityReactorBase implements IFluidHandler, PipeConnector, MultiBlockTile, FusionReactorToroidPart,
ToggleTile {

	private final HybridTank tank = new HybridTank("injector", 8000);

	private ForgeDirection facing;

	private boolean hasMultiBlock;

	private boolean enabled = true;

	public boolean hasMultiBlock() {
		return hasMultiBlock;
	}

	public void setHasMultiBlock(boolean has) {
		hasMultiBlock = has;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (DragonAPICore.debugtest) {
			tank.addLiquid(1000, FluidRegistry.getFluid("rc fusion plasma"));
			hasMultiBlock = true;
		}

		if (this.canMake())
			this.make(world, x, y, z);
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
	}

	private boolean canMake() {
		if (!hasMultiBlock)
			return false;
		if (tank.isEmpty())
			return false;
		if (!enabled)
			return false;
		if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			return false;
		return true;
	}

	private void make(World world, int x, int y, int z) {
		this.createPlasma(world, x, y, z);
		tank.removeLiquid(2);
	}

	private void createPlasma(World world, int x, int y, int z) {
		EntityPlasma e = new EntityPlasma(world, x, y, z, placer);
		e.setTarget(x+this.getFacing().offsetX, z+this.getFacing().offsetZ);
		if (!world.isRemote)
			world.spawnEntityInWorld(e);
	}

	public int[] getTarget() {
		int dx = xCoord+this.getFacing().offsetX;
		int dz = zCoord+this.getFacing().offsetZ;
		return new int[]{dx, yCoord, dz};
	}

	public FusionReactorToroidPart getNextPart(World world, int x, int y, int z) {
		int dx = xCoord+this.getFacing().offsetX*2;
		int dz = zCoord+this.getFacing().offsetZ*2;
		TileEntity te = world.getTileEntity(dx, y, dz);
		return te instanceof FusionReactorToroidPart ? (FusionReactorToroidPart)te : null;
	}

	public ForgeDirection getFacing() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && this.shouldFlip())
			return System.currentTimeMillis()%4000 >= 2000 ? ForgeDirection.NORTH : ForgeDirection.SOUTH;
			return facing != null ? facing : ForgeDirection.EAST;
	}

	@SideOnly(Side.CLIENT)
	private boolean shouldFlip() {
		return StructureRenderer.isRenderingTiles();
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry p, ForgeDirection side) {
		return true;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return Flow.INPUT;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int amount, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.getFluid("rc fusion plasma")) && this.getAdjacentTileEntity(from) instanceof TileEntityMagneticPipe;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public int getIndex() {
		return ReactorTiles.INJECTOR.ordinal();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setBoolean("multi", hasMultiBlock);

		NBT.setBoolean("t_enable", enabled);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);

		facing = dirs[NBT.getInteger("face")];

		hasMultiBlock = NBT.getBoolean("multi");

		if (NBT.hasKey("t_enable"))
			enabled = NBT.getBoolean("t_enable");
	}

	@Override
	public int getTextureState(ForgeDirection side) {
		return side == this.getFacing() ? 0 : side.offsetY != 0 ? 2 : 2;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		enabled = enable;
		this.syncAllData(false);
	}

	@Override
	public void breakBlock() {
		if (!worldObj.isRemote) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				int dx = xCoord+dir.offsetX;
				int dy = yCoord+dir.offsetY;
				int dz = zCoord+dir.offsetZ;
				Block b = worldObj.getBlock(dx, dy, dz);
				if (b instanceof BlockMultiBlock) {
					((BlockMultiBlock)b).breakMultiBlock(worldObj, dx, dy, dz);
				}
			}
		}
	}

}
