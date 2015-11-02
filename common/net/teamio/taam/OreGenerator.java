package net.teamio.taam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	public static class GenerationInfo {
		public WorldGenMinable gen;
		public int generateAbove;
		public int generateBelow;
		public int maxDepositCount;
		public GenerationInfo(WorldGenMinable gen, int generateAbove,
				int generateBelow, int maxDepositCount) {
			this.gen = gen;
			this.generateAbove = generateAbove;
			this.generateBelow = generateBelow;
			this.maxDepositCount = maxDepositCount;
		}
	}
	
	List<GenerationInfo> gens;
	
	public OreGenerator() {
		gens = new ArrayList<GenerationInfo>();
		// Copper Ore
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, 0, 14, Blocks.stone), 0, 64, 10));
		// Tin Ore
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, 1, 13, Blocks.stone), 0, 64, 10));
		// Native Aluminum
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, 2, 2, Blocks.stone), 0, 64, 3));
		// Bauxite
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, 3, 35, Blocks.stone), 0, 128, 10));
		gens.add(new GenerationInfo(new WorldGenMinable(TaamMain.blockOre, 3, 35, Blocks.dirt), 0, 128, 5));
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		default:
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {
	}

	private void generateNether(World world, Random random, int i, int j) {
	}

	private void generateSurface(World world, Random random, int i, int j) {
		for(GenerationInfo gen : gens) {
			for (int k = 0; k < gen.maxDepositCount; k++) {
				int firstBlockXCoord = i + random.nextInt(16);
				int firstBlockYCoord = gen.generateAbove + random.nextInt(gen.generateBelow - gen.generateAbove);
				int firstBlockZCoord = j + random.nextInt(16);
				gen.gen.generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
			}
		}
	}

}