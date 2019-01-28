package pcgen.output.json;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Movement;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Vision;
import pcgen.core.analysis.BonusCalc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.collections4.CollectionUtils;

public class RaceSerializer implements JsonSerializer<Race>
{

	private static final NumberFormat ADJ_FMT = new DecimalFormat("+0;-0"); //$NON-NLS-1$

	@Override
	public JsonElement serialize(Race race, Type typeOfSrc,
		JsonSerializationContext context)
	{
		JsonObject jsonRace = new JsonObject();

		CDOMSerializer.addStandardItems(jsonRace, race, context);

		//Types
		SerializerUtilities.writeList(jsonRace, "subtypes",
			race.getListFor(ListKey.RACESUBTYPE), context);
		jsonRace.addProperty("racetype", getRaceType(race));

		//FavoredClass
		jsonRace.addProperty("anyFavoredClass",
			race.getSafe(ObjectKey.ANY_FAVORED_CLASS));
		jsonRace.addProperty("selectFavoredClass",
			hasFavoredClassSelection(race));
		SerializerUtilities.writeList(jsonRace, "favoredClass",
			race.getListFor(ListKey.FAVORED_CLASS), context);

		//Movement
		List<Movement> movements = race.getListFor(ListKey.BASE_MOVEMENT);
		if (movements != null && !movements.isEmpty())
		{
			jsonRace.addProperty("move", movements.get(0).toString());
		}
		SerializerUtilities.writeList(jsonRace, "movement", movements, context);

		//Size
		jsonRace.addProperty("size", getSizeFormula(race));

		//Hit Dice
		jsonRace.addProperty("advancedUnlimited",
			race.isAdvancementUnlimited());
		jsonRace.addProperty("maxHitDiceAdvancement",
			race.maxHitDiceAdvancement());

		//Stat Locking
		CDOMSerializer.writeList(jsonRace, race, "statLocks",
			ListKey.STAT_LOCKS, context);
		CDOMSerializer.writeList(jsonRace, race, "unlockedStats",
			ListKey.UNLOCKED_STATS, context);
		CDOMSerializer.writeList(jsonRace, race, "nonStats",
			ListKey.NONSTAT_STATS, context);
		CDOMSerializer.writeList(jsonRace, race, "returnedStats",
			ListKey.NONSTAT_TO_STAT_STATS, context);
		CDOMSerializer.writeList(jsonRace, race, "minStatValues",
			ListKey.STAT_MINVALUE, context);
		CDOMSerializer.writeList(jsonRace, race, "maxStatValues",
			ListKey.STAT_MAXVALUE, context);

		PlayerCharacter pc = SerializerContext.pcContext.get();

		//Vision
		Collection<CDOMReference<Vision>> mods = CollectionUtils
			.emptyIfNull(race.getListMods(Vision.VISIONLIST));
		List<Vision> visionList =
				flattenReferenceCollection(mods).collect(Collectors.toList());
		JsonArray visionArray = new JsonArray();
		for (Vision vision : visionList)
		{
			visionArray.add(context.serialize(vision));
		}
		jsonRace.add("vision", visionArray);

		if (pc != null)
		{
			//Level Adjustment
			String levelAdjustment = ADJ_FMT.format(
				race.getSafe(FormulaKey.LEVEL_ADJUSTMENT).resolve(pc, ""));
			jsonRace.addProperty("levelAdjustment", levelAdjustment);

			//Description
			CDOMSerializer.writeDescription(jsonRace, race);

			//Details of Vision
			writeVisionDetail(jsonRace, pc, visionList);

			//Stat Bonuses
			writeStatBonus(jsonRace, pc, race);

			//Prerequisites
			CDOMSerializer.writePreReq(jsonRace, race);
		}
		return jsonRace;
	}

	private void writeVisionDetail(JsonObject jsonRace, PlayerCharacter pc,
		List<Vision> visionList)
	{
		JsonArray visionArray = new JsonArray();
		for (Vision vision : visionList)
		{
			if (pc.isQualified(vision))
			{
				JsonObject jsonVision = new JsonObject();
				jsonVision.addProperty(vision.getType().toString(),
					vision.getDistance().resolve(pc, "").intValue());
				visionArray.add(jsonVision);
			}
		}
		jsonRace.add("resolvedVision", visionArray);
	}

	private void writeStatBonus(JsonObject jsonRace, PlayerCharacter pc,
		Race race)
	{
		JsonObject statDetail = new JsonObject();
		for (PCStat stat : pc.getStatSet())
		{
			if (BonusCalc.getStatMod(race, stat, pc) != 0)
			{
				statDetail.addProperty(stat.getKeyName(),
					+BonusCalc.getStatMod(race, stat, pc));
			}
		}
		jsonRace.add("statBonus", statDetail);
	}

	private Stream<Vision> flattenReferenceCollection(
		Collection<CDOMReference<Vision>> mods)
	{
		return mods.stream().flatMap(ref -> ref.getContainedObjects().stream());
	}

	private String getSizeFormula(Race race)
	{
		Formula formula = race.get(FormulaKey.SIZE);
		return (formula == null) ? "" : formula.toString();
	}

	private boolean hasFavoredClassSelection(Race race)
	{
		return race.getSafeListFor(ListKey.NEW_CHOOSE_ACTOR).stream().filter(
			actor -> actor.getClass().getSimpleName().equals("FavclassToken"))
			.findFirst().isPresent();
	}

	private String getRaceType(Race race)
	{
		RaceType rt = race.getSafe(ObjectKey.RACETYPE);
		return (rt == null) ? "" : rt.toString();
	}
}
