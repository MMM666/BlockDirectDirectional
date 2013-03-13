package net.minecraft.src;

import java.util.Random;

public class BDD_Client {

	public static void showFX(World par1World, int par2, int par3, int par4, Random par5Random, int pMetadata) {
		// エフェクトは作るの面倒なので有り物使い回し
		EntityFX lfx;
		// 湯気
		lfx = new EntitySmokeFX(par1World,
				par2 + par5Random.nextDouble() * 0.6D + 0.2D,
				par3 + (6D + (double)pMetadata * 3D) / 16F,
				par4 + par5Random.nextDouble() * 0.6D + 0.2D, 0D, 0D, 0D);
		lfx.setRBGColorF(1.0F, 1.0F, 1.0F);
		lfx.setAlphaF(0.4F);
		MMM_Helper.mc.effectRenderer.addEffect(lfx);
		// 泡
		lfx = new EntityHeartFX(par1World,
				par2 + par5Random.nextDouble() * 0.6D + 0.2D,
				par3 + 0.3D,
				par4 + par5Random.nextDouble() * 0.6D + 0.2D, 0D, 0D, 0D, 0.3F);
//		CustomColorizer.updateWaterFX(lfx, par1World);
		lfx.setParticleTextureIndex(32);
		lfx.particleMaxAge = pMetadata == 3 ? 15 : pMetadata == 2 ? 7 : 3;
		MMM_Helper.mc.effectRenderer.addEffect(lfx);
	}

}
