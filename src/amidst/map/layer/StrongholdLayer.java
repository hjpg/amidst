package amidst.map.layer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class StrongholdLayer extends IconLayer {
	// @formatter:off
	private static final List<Biome> VALID_BIOMES_DEFAULT = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland
	);
	
	private static final List<Biome> VALID_BIOMES_1_0 = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains
	);
	
	private static final List<Biome> VALID_BIOMES_1_1 = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains,
			Biome.desertHills,
			Biome.forestHills,
			Biome.extremeHillsEdge
	);
	
	private static final List<Biome> VALID_BIOMES_12w03a = Arrays.asList(
			Biome.desert,
			Biome.forest,
			Biome.extremeHills,
			Biome.swampland,
			Biome.taiga,
			Biome.icePlains,
			Biome.iceMountains,
			Biome.desertHills,
			Biome.forestHills,
			Biome.extremeHillsEdge,
			Biome.jungle,
			Biome.jungleHills
	);
	// @formatter:on

	private MapObject[] strongholds = new MapObject[3];
	private Random random = new Random();

	@Override
	public boolean isVisible() {
		return Options.instance.showStrongholds.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				generateAt(fragment, x, y);
			}
		}
	}

	private void generateAt(Fragment fragment, int x, int y) {
		for (int i = 0; i < 3; i++) {
			if (fragment.isInBounds(strongholds[i])) {
				fragment.addObject(strongholds[i]);
			}
		}
	}

	@Override
	public void reload() {
		findStrongholds();
	}

	private void findStrongholds() {
		List<Biome> validBiomes = getValidBiomes();
		updateSeed();
		double angle = initAngle();
		for (int i = 0; i < 3; i++) {
			double distance = nextDistance();
			int x = getX(angle, distance) << 4;
			int y = getY(angle, distance) << 4;
			Point strongholdLocation = findStronghold(validBiomes, x, y);
			if (strongholdLocation != null) {
				strongholds[i] = createMapObject(strongholdLocation.x,
						strongholdLocation.y);
			} else {
				strongholds[i] = createMapObject(x, y);
			}
			angle = updateAngle(angle);
		}
	}

	private MapObject createMapObject(int x, int y) {
		return MapObject.fromWorldCoordinates(Options.instance.showStrongholds,
				MapMarkers.STRONGHOLD, x, y);
	}

	private Point findStronghold(List<Biome> validBiomes, int x, int y) {
		return MinecraftUtil.findValidLocation(x + 8, y + 8, 112, validBiomes,
				random);
	}

	private int getY(double angle, double distance) {
		return (int) Math.round(Math.sin(angle) * distance);
	}

	private int getX(double angle, double distance) {
		return (int) Math.round(Math.cos(angle) * distance);
	}

	private double nextDistance() {
		return (1.25D + random.nextDouble()) * 32.0D;
	}

	private double initAngle() {
		return random.nextDouble() * 3.141592653589793D * 2.0D;
	}

	private double updateAngle(double angle) {
		return angle + 6.283185307179586D / 3.0D;
	}

	private void updateSeed() {
		random.setSeed(Options.instance.world.getSeed());
	}

	private List<Biome> getValidBiomes() {
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V13w36a)) {
			return getValidBiomesV13w36a();
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w03a)) {
			return VALID_BIOMES_12w03a;
		} else if (MinecraftUtil.getVersion() == VersionInfo.V1_1) {
			return VALID_BIOMES_1_1;
		} else if (MinecraftUtil.getVersion() == VersionInfo.V1_9pre6
				|| MinecraftUtil.getVersion() == VersionInfo.V1_0) {
			return VALID_BIOMES_1_0;
		} else {
			return VALID_BIOMES_DEFAULT;
		}
	}

	private List<Biome> getValidBiomesV13w36a() {
		List<Biome> result = new ArrayList<Biome>();
		for (Biome biome : Biome.biomes) {
			if (isValidBiomeV13w36a(biome)) {
				result.add(biome);
			}
		}
		return result;
	}

	private boolean isValidBiomeV13w36a(Biome biome) {
		return biome != null && biome.type.value1 > 0;
	}

	@Deprecated
	public MapObject[] getStrongholds() {
		return strongholds;
	}
}
