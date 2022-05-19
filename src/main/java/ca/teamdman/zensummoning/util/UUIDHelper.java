package ca.teamdman.zensummoning.util;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UUIDHelper {
	public static Optional<UUID> fromString(String uuid) {
		try {
			return Optional.of(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public static ListTag serialize(Collection<UUID> collection) {
		return collection.stream()
				.map(Object::toString)
				.map(StringTag::valueOf)
				.collect(Collectors.toCollection(ListTag::new));
	}

	public static Stream<UUID> deserialize(ListTag uuidList) {
		return uuidList.stream()
				.map(StringTag.class::cast)
				.map(StringTag::getAsString)
				.map(UUIDHelper::fromString)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}
}
