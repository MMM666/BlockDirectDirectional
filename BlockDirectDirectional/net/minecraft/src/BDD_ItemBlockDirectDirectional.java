package net.minecraft.src;

public class BDD_ItemBlockDirectDirectional extends ItemBlock {

	public BDD_ItemBlockDirectDirectional(int i) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return mod_BDD_BlockDirectDirectional.directDirectional.getBlockTextureFromSideAndMetadata(2, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

}
