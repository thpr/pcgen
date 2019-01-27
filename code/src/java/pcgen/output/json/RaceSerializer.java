package pcgen.output.json;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.collections4.CollectionUtils;

public class RaceSerializer extends StdSerializer<Race>
{
	private static final NumberFormat ADJ_FMT = new DecimalFormat("+0;-0"); //$NON-NLS-1$

	public RaceSerializer()
	{
		this(null);
	}

	public RaceSerializer(Class<Race> t)
	{
		super(t);
	}

	@Override
	public void serialize(Race race, JsonGenerator jsonGenerator,
		SerializerProvider serializer) throws IOException
	{
		//Note jsonGenerator.getOutputContext() can be used to determine whether to write items beyond the identifier

		jsonGenerator.writeStartObject();
		CDOMSerializer.addStandardItems(jsonGenerator, race);

		//Types
		SerializerUtilities.writeList(jsonGenerator, "subtypes",
			race.getListFor(ListKey.RACESUBTYPE));
		jsonGenerator.writeStringField("racetype", getRaceType(race));

		//FavoredClass
		jsonGenerator.writeBooleanField("anyFavoredClass",
			race.getSafe(ObjectKey.ANY_FAVORED_CLASS));
		jsonGenerator.writeBooleanField("selectFavoredClass",
			hasFavoredClassSelection(race));
		SerializerUtilities.writeList(jsonGenerator, "favoredClass",
			race.getListFor(ListKey.FAVORED_CLASS));

		//Movement
		List<Movement> movements = race.getListFor(ListKey.BASE_MOVEMENT);
		if (movements != null && !movements.isEmpty())
		{
			jsonGenerator.writeStringField("move", movements.get(0).toString());
		}
		SerializerUtilities.writeList(jsonGenerator, "movement", movements);

		//Size
		jsonGenerator.writeStringField("size", getSizeFormula(race));

		//Hit Dice
		jsonGenerator.writeBooleanField("advancedUnlimited",
			race.isAdvancementUnlimited());
		jsonGenerator.writeNumberField("maxHitDiceAdvancement",
			race.maxHitDiceAdvancement());

		//Stat Locking
		CDOMSerializer.writeList(jsonGenerator, race, "statLocks",
			ListKey.STAT_LOCKS);
		CDOMSerializer.writeList(jsonGenerator, race, "unlockedStats",
			ListKey.UNLOCKED_STATS);
		CDOMSerializer.writeList(jsonGenerator, race, "nonStats",
			ListKey.NONSTAT_STATS);
		CDOMSerializer.writeList(jsonGenerator, race, "returnedStats",
			ListKey.NONSTAT_TO_STAT_STATS);
		CDOMSerializer.writeList(jsonGenerator, race, "minStatValues",
			ListKey.STAT_MINVALUE);
		CDOMSerializer.writeList(jsonGenerator, race, "maxStatValues",
			ListKey.STAT_MAXVALUE);

		PlayerCharacter pc = SerializerContext.pcContext.get();

		//Vision
		Collection<CDOMReference<Vision>> mods = CollectionUtils
			.emptyIfNull(race.getListMods(Vision.VISIONLIST));
		List<Vision> visionList =
				flattenReferenceCollection(mods).collect(Collectors.toList());
		jsonGenerator.writeArrayFieldStart("vision");
		for (Vision vision : visionList)
		{
			jsonGenerator.writeObject(vision);
		}
		jsonGenerator.writeEndArray();

		if (pc != null)
		{
			//Level Adjustment
			String levelAdjustment = ADJ_FMT.format(
				race.getSafe(FormulaKey.LEVEL_ADJUSTMENT).resolve(pc, ""));
			jsonGenerator.writeStringField("levelAdjustment", levelAdjustment);

			//Description
			CDOMSerializer.writeDescription(jsonGenerator, race);

			//Details of Vision
			writeVisionDetail(jsonGenerator, pc, visionList);

			//Stat Bonuses
			writeStatBonus(jsonGenerator, pc, race);

			//Prerequisites
			CDOMSerializer.writePreReq(jsonGenerator, race);
		}

		jsonGenerator.writeEndObject();
	}

	private void writeVisionDetail(JsonGenerator jsonGenerator,
		PlayerCharacter pc, List<Vision> visionList) throws IOException
	{
		jsonGenerator.writeArrayFieldStart("resolvedVision");
		for (Vision vision : visionList)
		{
			if (pc.isQualified(vision))
			{
				jsonGenerator.writeStartObject();
				jsonGenerator.writeNumberField(vision.getType().toString(),
					vision.getDistance().resolve(pc, "").intValue());
				jsonGenerator.writeEndObject();
			}
		}
		jsonGenerator.writeEndArray();
	}

	private void writeStatBonus(JsonGenerator jsonGenerator, PlayerCharacter pc,
		Race race) throws IOException
	{
		boolean hasBonus = false;
		for (PCStat stat : pc.getStatSet())
		{
			if (BonusCalc.getStatMod(race, stat, pc) != 0)
			{
				if (!hasBonus)
				{
					jsonGenerator.writeArrayFieldStart("statBonuses");
					hasBonus = true;
				}
				jsonGenerator.writeNumberField(stat.getKeyName(),
					+BonusCalc.getStatMod(race, stat, pc));
			}
		}
		if (hasBonus)
		{
			jsonGenerator.writeEndArray();
		}
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
