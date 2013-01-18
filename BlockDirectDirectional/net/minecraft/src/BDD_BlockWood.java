package net.minecraft.src;

public class BDD_BlockWood extends BlockWood {

	public BDD_BlockWood(int par1) {
		super(par1);
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		// 向きに応じたメタデータを貼り付ける
		int l = determineOrientation(world, i, j, k, (EntityPlayer)entityliving);
		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	private static int determineOrientation(World world, int i, int j, int k, EntityLiving entityliving) {
		// ピストンあたりの丸パクリ
		int ll = world.getBlockMetadata(i, j, k);

		if(MathHelper.abs((float)entityliving.posX - (float)i) < 2.0F && MathHelper.abs((float)entityliving.posZ - (float)k) < 2.0F) {
			double d = (entityliving.posY + 1.82D) - (double)entityliving.yOffset;
			// 上下
			if ((d - (double)j > 2D) || ((double)j - d > 0.0D)) {
				ll |= mod_BDD_BlockDirectDirectional.BLD_Vrt;
			}
		}
		int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		switch (l) {
		case 0:
		case 2:
			ll |= mod_BDD_BlockDirectDirectional.BLD_Hor;
		}
		return ll;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int par1, int par2) {
		return super.getBlockTextureFromSideAndMetadata(par1, par2 & 0x03);
	}

	@Override
	public int damageDropped(int i) {
		return i & 0x03;
	}

}
