package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class BDD_BlockDirectDirectional extends Block {

	public BDD_BlockDirectDirectional(int i, Material material) {
		super(i, material);
		blockIndexInTexture = mod_BDD_BlockDirectDirectional.sprite[0];
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		// 向きに応じたメタデータを貼り付ける
		int l = determineOrientation(world, i, j, k, entityliving);
		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	private static int determineOrientation(World world, int i, int j, int k, EntityLiving entityliving) {
		// ピストンあたりの丸パクリ
		int ll = world.getBlockMetadata(i, j, k);

		if(MathHelper.abs((float)entityliving.posX - (float)i) < 2.0F && MathHelper.abs((float)entityliving.posZ - (float)k) < 2.0F) {
			double d = (entityliving.posY + entityliving.getEyeHeight()) - (double)entityliving.yOffset;
			// 上下
			if ((d - (double)j > 2D) || ((double)j - d > 0.0D)) {
				ll |= mod_BDD_BlockDirectDirectional.BLD_Vrt;
			}
		}

		int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		switch (l) {
		case 0:
		case 2:
			return ll |= mod_BDD_BlockDirectDirectional.BLD_Hor;
		}
		return ll;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return mod_BDD_BlockDirectDirectional.sprite[j & 0x03];
	}

	@Override
	public int damageDropped(int i) {
		return i & 0x03;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		if (mod_BDD_BlockDirectDirectional.sprite[0] > 0) {
			par3List.add(new ItemStack(par1, 1, 0));
		}
		if (mod_BDD_BlockDirectDirectional.sprite[1] > 0) {
			par3List.add(new ItemStack(par1, 1, 1));
		}
		if (mod_BDD_BlockDirectDirectional.sprite[2] > 0) {
			par3List.add(new ItemStack(par1, 1, 2));
		}
		if (mod_BDD_BlockDirectDirectional.sprite[3] > 0) {
			par3List.add(new ItemStack(par1, 1, 3));
		}
	}

}
