package ca.teamdman.zensummoning.util;

import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

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

	public static ListNBT serialize(Collection<UUID> collection) {
		return collection.stream()
				.map(Object::toString)
				.map(StringNBT::valueOf)
				.collect(Collectors.toCollection(ListNBT::new));
	}

	public static Stream<UUID> deserialize(ListNBT uuidList) {
		return uuidList.stream()
				.map(StringNBT.class::cast)
				.map(StringNBT::getString)
				.map(UUIDHelper::fromString)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}
}
