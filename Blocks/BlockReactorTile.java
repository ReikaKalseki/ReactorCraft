/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Auxiliary.ReactorStacks;
import Reika.ReactorCraft.Auxiliary.Temperatured;
import Reika.ReactorCraft.Base.TileEntityNuclearCore;
import Reika.ReactorCraft.Base.TileEntityReactorBase;
import Reika.ReactorCraft.Blocks.Multi.BlockSolenoidMulti;
import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.TileEntityHeavyPump;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityReactorBoiler;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell.LiquidStates;
import Reika.ReactorCraft.TileEntities.Fission.Breeder.TileEntitySodiumHeater;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntitySolenoidMagnet;
import Reika.ReactorCraft.TileEntities.Fusion.TileEntityToroidMagnet;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityUProcessor;
import Reika.RotaryCraft.API.Interfaces.ThermalMachine;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.RotaryAux;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Registry.ItemRegistry;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockReactorTile extends BlockTEBase implements MachineRegistryBlock, IWailaDataProvider {

	protected static final IIcon[][][] icons = new IIcon[ReactorTiles.TEList.length][6][16];

	public BlockReactorTile(Material par2Material) {
		super(par2Material);
		this.setHardness(2F);
		this.setResistance(10F);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		if (te instanceof TileEntityNuclearCore) {
			TileEntityNuclearCore tile = (TileEntityNuclearCore)te;
			if (tile.isActive()) {
				if (ReikaWorldHelper.isSubmerged(iba, x, y, z)) {
					return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(0x0077ff, 15) : 15;
				}
			}
		}
		else if (te instanceof TileEntityToroidMagnet) {
			TileEntityToroidMagnet tile = (TileEntityToroidMagnet)te;
			if (tile.isActive()) {
				return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(0x0077ff, 15) : 15;
			}
		}
		return 0;
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		TileEntity te = ReactorTiles.createTEFromIDAndMetadata(this, meta);
		return te;
	}

	@Override
	public final boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		if (ReactorCraft.instance.isLocked())
			return;
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			if (!r.hasRender() && !r.isPipe()) {
				if (r.hasTextureStates()) {
					for (int k = 0; k < r.getTextureStates(); k++) {
						if (r.hasSidedTextures()) {
							for (int j = 0; j < 6; j++) {
								icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_"+j+"_#"+k);
							}
						}
						else if (r.isEndTextured()) {
							for (int j = 0; j < 2; j++) {
								String s = "ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_top"+"_#"+k;
								if (j == 0 && !r.isTopSameTextureAsBottom()) {
									s = "ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_bottom"+"_#"+k;
								}
								icons[i][j][k] = ico.registerIcon(s);

							}
							for (int j = 2; j < 6; j++) {
								icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_#"+k);
							}
						}
						else {
							for (int j = 0; j < 6; j++) {
								icons[i][j][k] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_#"+k);
							}
						}
					}
				}
				else {
					if (r.hasSidedTextures()) {
						for (int j = 0; j < 6; j++) {
							icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_"+j);
						}
					}
					else if (r.isEndTextured()) {
						for (int j = 0; j < 2; j++) {
							String s = "ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_top";
							if (j == 0 && !r.isTopSameTextureAsBottom()) {
								s = "ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH)+"_bottom";
							}
							icons[i][j][0] = ico.registerIcon(s);
						}
						for (int j = 2; j < 6; j++) {
							icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH));
						}
					}
					else {
						for (int j = 0; j < 6; j++) {
							icons[i][j][0] = ico.registerIcon("ReactorCraft:"+r.name().toLowerCase(Locale.ENGLISH));
						}
					}
				}
			}
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		ReactorTiles r = ReactorTiles.getTE(iba, x, y, z);
		if (r == null)
			return null;
		if (r.hasTextureStates()) {
			TileEntityReactorBase te = (TileEntityReactorBase)iba.getTileEntity(x, y, z);
			int k = te.getTextureState(ForgeDirection.VALID_DIRECTIONS[s]);
			return icons[r.ordinal()][s][k];
		}
		else {
			return icons[r.ordinal()][s][0];
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		//for drops, needs to be r.ordinal(), not metadata
		ReactorTiles r = ReactorTiles.getMachineFromIDandMetadata(this, meta);
		int top = 0;
		if (r != ReactorTiles.COOLANT && r.getTextureStates() == 5)
			top = 4;
		else if (r.getTextureStates() == 4)
			top = 0;
		return r != null ? icons[r.ordinal()][s][s <= 1 ? top : 0] : icons[meta][s][0];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9) {
		super.onBlockActivated(world, x, y, z, ep, par6, par7, par8, par9);
		if (ReactorCraft.instance.isLocked())
			return false;
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityBase)
			((TileEntityBase)tile).syncAllData(true);

		ItemStack is = ep.getCurrentEquippedItem();
		if (ep.isSneaking())
			return false;
		if (is != null) {
			ItemRegistry i = ItemRegistry.getEntry(is);
			if (i != null) {
				if (i.overridesRightClick(is))
					return false;
			}
			if (is.getItem() == ReactorItems.REMOTE.getItemInstance())
				return false;
		}
		if (r == ReactorTiles.COOLANT && is != null && is.stackSize == 1) {
			TileEntityWaterCell te = (TileEntityWaterCell)world.getTileEntity(x, y, z);
			switch(te.getLiquidState()) {
				case EMPTY:
					if (is.getItem() == Items.water_bucket) {
						te.setLiquidState(LiquidStates.WATER);
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
						return true;
					}
					else if (is.getItem() == ReactorItems.BUCKET.getItemInstance()) {
						te.setLiquidState(LiquidStates.HEAVY);
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
						return true;
					}
					else if (ReikaItemHelper.matchStacks(is, ReactorStacks.nacan)) {
						te.setLiquidState(LiquidStates.SODIUM);
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan);
						return true;
					}
					else if (ReikaItemHelper.matchStacks(is, ReactorStacks.lifbecan)) {
						te.setLiquidState(LiquidStates.LITHIUM);
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan);
						return true;
					}
					break;
				case WATER:
					if (is.getItem() == Items.bucket) {
						te.setLiquidState(LiquidStates.EMPTY);
						ep.setCurrentItemOrArmor(0, new ItemStack(Items.water_bucket));
						return true;
					}
					break;
				case HEAVY:
					if (is.getItem() == Items.bucket) {
						te.setLiquidState(LiquidStates.EMPTY);
						ep.setCurrentItemOrArmor(0, ReactorItems.BUCKET.getStackOf());
						return true;
					}
					break;
				case SODIUM:
					if (ReikaItemHelper.matchStacks(is, ReactorStacks.emptycan)) {
						te.setLiquidState(LiquidStates.EMPTY);
						ep.setCurrentItemOrArmor(0, ReactorStacks.nacan);
						return true;
					}
				case LITHIUM:
					if (ReikaItemHelper.matchStacks(is, ReactorStacks.emptycan)) {
						te.setLiquidState(LiquidStates.EMPTY);
						ep.setCurrentItemOrArmor(0, ReactorStacks.lifbecan);
						return true;
					}
			}
		}
		if (r == ReactorTiles.SYNTHESIZER && is != null && is.stackSize == 1) {
			TileEntitySynthesizer te = (TileEntitySynthesizer)world.getTileEntity(x, y, z);
			if (is.getItem() == Items.water_bucket) {
				boolean flag = te.addWater(1000);
				if (flag && !ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
				return true;
			}
		}
		if (r == ReactorTiles.ELECTROLYZER && is != null && is.stackSize == 1) {
			TileEntityElectrolyzer te = (TileEntityElectrolyzer)world.getTileEntity(x, y, z);
			if (ReikaItemHelper.matchStacks(is, ReactorItems.BUCKET.getStackOf())) {
				boolean flag = te.addHeavyWater(1000);
				if (flag && !ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
				return true;
			}
		}
		if (r == ReactorTiles.FLUIDEXTRACTOR && is != null && is.stackSize == 1) {
			TileEntityHeavyPump te = (TileEntityHeavyPump)world.getTileEntity(x, y, z);
			if (te.hasABucket()) {
				if (is.getItem() == Items.bucket && te.getFluid() == FluidRegistry.getFluid("rc heavy water")) {
					te.subtractBucket();
					ep.setCurrentItemOrArmor(0, ReactorItems.BUCKET.getStackOf());
					return true;
				}
				else if (ReikaItemHelper.matchStacks(is, ReactorStacks.emptycan) && te.getFluid() == FluidRegistry.getFluid("rc lithium")) {
					te.subtractBucket();
					ep.setCurrentItemOrArmor(0, ReactorStacks.lican.copy());
					return true;
				}
			}
		}
		if (r == ReactorTiles.SODIUMBOILER && is != null && is.stackSize == 1) {
			TileEntitySodiumHeater te = (TileEntitySodiumHeater)world.getTileEntity(x, y, z);
			if (te.getLevel()+FluidContainerRegistry.BUCKET_VOLUME <= te.getCapacity()) {
				if (ReikaItemHelper.matchStacks(is, ReactorStacks.nacan)) {
					if (te.getLevel() <= 0 || te.getContainedFluid().equals(FluidRegistry.getFluid("rc sodium"))) {
						te.addLiquid(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.getFluid("rc sodium"));
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
					}
					return true;
				}
			}
		}
		if (r == ReactorTiles.BOILER && is != null && is.stackSize == 1) {
			TileEntityReactorBoiler te = (TileEntityReactorBoiler)world.getTileEntity(x, y, z);
			if (te.getLevel()+FluidContainerRegistry.BUCKET_VOLUME <= te.getCapacity()) {
				if (is.getItem() == Items.water_bucket) {
					if (te.getLevel() <= 0 || te.getContainedFluid().equals(FluidRegistry.WATER)) {
						te.addLiquid(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.WATER);
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
					}
					return true;
				}
				if (ReikaItemHelper.matchStacks(is, ReactorStacks.nh3can)) {
					if (te.getLevel() <= 0 || te.getContainedFluid().equals(FluidRegistry.getFluid("rc ammonia"))) {
						te.addLiquid(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.getFluid("rc ammonia"));
						if (!ep.capabilities.isCreativeMode)
							ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan);
					}
					return true;
				}
			}
		}
		if (r == ReactorTiles.PROCESSOR && is != null && is.getItem() == ReactorItems.CANISTER.getItemInstance() && is.stackSize == 1) {
			TileEntityUProcessor te = (TileEntityUProcessor)world.getTileEntity(x, y, z);
			if (is.getItemDamage() == ReactorStacks.emptycan.getItemDamage() && te.getOutput() >= FluidContainerRegistry.BUCKET_VOLUME) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, /*te.getOutputFluid() == FluidRegistry.getFluid("rc uranium hexafluoride") ? ReactorStacks.uf6can.copy() : ReactorStacks.lifbecan.copy()*/ReikaFluidHelper.getAllContainersFor(te.getOutputFluid()).get(0));
				te.drain(null, FluidContainerRegistry.BUCKET_VOLUME, true);
			}
			else if (is.getItemDamage() == ReactorStacks.hfcan.getItemDamage() && te.canAcceptMoreIntermediate(FluidContainerRegistry.BUCKET_VOLUME)) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan.copy());
				te.addIntermediate(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.getFluid("rc hydrofluoric acid"));
			}
			return true;
		}
		if (r == ReactorTiles.CENTRIFUGE && is != null && is.getItem() == ReactorItems.CANISTER.getItemInstance() && is.stackSize == 1) {
			TileEntityCentrifuge te = (TileEntityCentrifuge)world.getTileEntity(x, y, z);
			if (is.getItemDamage() == ReactorStacks.emptycan.getItemDamage() && te.getUF6() >= FluidContainerRegistry.BUCKET_VOLUME) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.uf6can.copy());
				te.removeFluid(FluidContainerRegistry.BUCKET_VOLUME);
			}
			else if (is.getItemDamage() == ReactorStacks.uf6can.getItemDamage() && te.canAcceptMoreUF6(FluidContainerRegistry.BUCKET_VOLUME)) {
				if (!ep.capabilities.isCreativeMode)
					ep.setCurrentItemOrArmor(0, ReactorStacks.emptycan.copy());
				te.addUF6(FluidContainerRegistry.BUCKET_VOLUME);
			}
			return true;
		}
		if (r.isTurbine()) {
			if (is != null && is.stackSize == 1 && ReikaItemHelper.matchStacks(is, ItemStacks.lubebucket)) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x, y, z);
				int amt = 1000;
				if (te.canAcceptLubricant(amt)) {
					te.addLubricant(amt);
					if (!ep.capabilities.isCreativeMode)
						ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
				}
			}
		}

		if (ReactorCraft.hasGui(world, x, y, z, ep)) {
			ep.openGui(ReactorCraft.instance, 0, world, x, y, z);
			return true;
		}

		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition vec, World world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, vec.blockX, vec.blockY, vec.blockZ);
		return r != null ? r.getCraftedProduct() : null;
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntitySolenoidMagnet) {
			((TileEntitySolenoidMagnet) te).removeFromToroids();
			Block id = world.getBlock(x, y-1, z);
			if (id == ReactorBlocks.SOLENOIDMULTI.getBlockInstance()) {
				BlockSolenoidMulti b = (BlockSolenoidMulti)ReactorBlocks.SOLENOIDMULTI.getBlockInstance();
				b.breakMultiBlock(world, x, y-1, z);
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		ReactorTiles r = ReactorTiles.getTE(world, x, y, z);
		if (r == ReactorTiles.HEATER)
			return 0;
		return 255;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		if (!player.capabilities.isCreativeMode && this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		if (world.getBlock(x, y, z) == this)
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getDrops(world, x, y, z, meta, 0));
	}

	protected boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (this instanceof BlockDuct)
			return true;
		return RotaryAux.canHarvestSteelMachine(ep);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		ReactorTiles r = ReactorTiles.getMachineFromIDandMetadata(this, meta);
		if (r != null) {
			if (r.isTurbine()) {
				TileEntityTurbineCore te = (TileEntityTurbineCore)world.getTileEntity(x, y, z);
				if (te == null)
					return li;
				if (te.getDamage() > 0) {
					li.add(ItemStacks.shaftcore.copy());
					for (int i = 0; i < ReikaRandomHelper.getSafeRandomInt(24-te.getDamage()); i++) {
						li.add(ItemStacks.prop.copy());
					}
				}
				else {
					ItemStack is = r.getCraftedProduct();
					if (te.getLubricant() > 0) {
						is.stackTagCompound = new NBTTagCompound();
						int lube = ReikaMathLibrary.roundDownToX(10, te.getLubricantToDrop()); //to help with stacking
						is.stackTagCompound.setInteger("lube", lube);
					}
					li.add(is);
				}
			}
			else {
				li.add(r.getCraftedProduct());
			}
		}
		return li;
	}

	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;//ReactorTiles.getMachineFromIDandMetadata(this, accessor.getMetadata()).getCraftedProduct();
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}*/
		return currenttip;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack itemStack, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		//if (/*LegacyWailaHelper.cacheAndReturn(acc)*/!tip.isEmpty())
		//	return tip;
		TileEntity te = acc.getTileEntity();
		if (te instanceof TileEntityReactorBase)
			((TileEntityBase)te).syncAllData(false);
		if (te instanceof Temperatured)
			tip.add(String.format("Temperature: %dC", ((Temperatured) te).getTemperature()));
		else if (te instanceof TemperatureTE)
			tip.add(String.format("Temperature: %dC", ((TemperatureTE) te).getTemperature()));
		else if (te instanceof ThermalMachine)
			tip.add(String.format("Temperature: %dC", ((ThermalMachine) te).getTemperature()));
		if (te instanceof IFluidHandler) {
			FluidTankInfo[] tanks = ((IFluidHandler)te).getTankInfo(ForgeDirection.UP);
			if (tanks != null) {
				for (int i = 0; i < tanks.length; i++) {
					FluidTankInfo info = tanks[i];
					FluidStack fs = info.fluid;
					String input = fs != null ? String.format("%d/%d mB of %s", fs.amount, info.capacity, fs.getFluid().getLocalizedName(fs)) : "Empty";
					tip.add("Tank "+i+": "+input);
				}
			}
		}
		if (te instanceof TileEntityToroidMagnet) {
			tip.add(String.format("Charge: %dkV", ((TileEntityToroidMagnet)te).getCharge()));
			tip.add(String.format("Coolant: %dmB", ((TileEntityToroidMagnet)te).getCoolant()));
			tip.add(String.format("Direction: %s", ((TileEntityToroidMagnet)te).getAim().toString()));
		}
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ReactorCraft");*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	@Override
	public final TileEnum getMachine(IBlockAccess world, int x, int y, int z) {
		return ReactorTiles.getTE(world, x, y, z);
	}

}
