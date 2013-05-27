/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.utils

import java.nio._
import java.util.UUID
import java.util.regex.Pattern

import org.apache.commons.lang3.Validate._
import org.apache.commons.lang3.StringUtils._

import com.google.common.base._

import edu.sabanciuniv.sentilab.core.models.UniquelyIdentifiable

/**
 * Provides some basic utilities for manipulating UUIDs.
 * @author Mus'ab Husaini
 */
object UuidUtils {
	private abstract class Uuid {
		def isUuid = toUUID != null
		def toBytes: Array[Byte]
		def toUUID: UUID
	}
	
	private case class UuidString(id: Option[String]) extends Uuid {
		override def toBytes = UuidUUID(Option(toUUID)).toBytes
		
		override def toUUID = {
			val Uuid = "^([a-f0-9]{8})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]{12})$".r
			toString match {
			  	case Uuid(u1, u2, u3, u4, u5) => {
			  		try {
			  			UUID.fromString("%s-%s-%s-%s-%s".format(u1, u2, u3, u4, u5))
			  		} catch {
			  		  	case _: Throwable => null
			  		}
			  	}
			  	case _ => null
			}
		}
		
		override def toString = id match {
		  	case Some(id) => id.replace("-", "").toLowerCase.trim
		  	case None => null
		}
	}
	
	private case class UuidBytes(id: Option[Array[Byte]]) extends Uuid {
		override def toBytes = id.getOrElse(null)
		
		override def toUUID = {
			try {
				val bb = ByteBuffer.wrap(toBytes)
				new UUID(bb.getLong, bb.getLong)
			} catch {
			  	case _: Throwable => null
			}
		}
		
		override def toString = UuidUUID(Option(toUUID)).toString
	}
	
	private case class UuidUUID(id: Option[UUID]) extends Uuid {
		override def toBytes = try {
			val bb = ByteBuffer.allocate(16)
			bb.putLong(toUUID.getMostSignificantBits)
			bb.putLong(toUUID.getLeastSignificantBits)
			bb.array
		} catch {
		  	case _: Throwable => null
		}
		
		override def toUUID = id.getOrElse(null)
		
		override def toString = id match {
			case Some(id) => UuidString(Option(id.toString)).toString
		  	case None => null
		}
	}

	/**
	 * Gets a function for converting {@link UUID} objects to strings.
	 * @return the {@link Function} for such conversion.
	 */
	def uuidToStringFunction: Function[UUID, String] = {
		new Function[UUID, String] {
			override def apply(input: UUID) = UuidUUID(Option(input)).toString
		};
	}
	
	/**
	 * Gets a function for converting {@link UUID} objects to UUID bytes.
	 * @return the {@link Function} for such conversion.
	 */
	def uuidToUuidBytesFunction: Function[UUID, Array[Byte]] = {
		new Function[UUID, Array[Byte]] {
			override def apply(input: UUID) = UuidUUID(Option(input)).toBytes
		};
	}
	
	/**
	 * Gets a function for converting UUID bytes to strings.
	 * @return the {@link Function} for such conversion.
	 */
	def uuidBytesToStringFunction: Function[Array[Byte], String] = {
		new Function[Array[Byte], String] {
			override def apply(input: Array[Byte]) = UuidBytes(Option(input)).toString
		};
	}
	
	/**
	 * Gets a function for converting UUID bytes to {@link UUID} objects.
	 * @return the {@link Function} for such conversion.
	 */
	def uuidBytesToUUIDFunction: Function[Array[Byte], UUID] = {
		new Function[Array[Byte], UUID] {
			override def apply(input: Array[Byte]) = UuidBytes(Option(input)).toUUID
		};
	}

	/**
	 * Gets a function for converting {@link UniquelyIdentifiable} instances to their {@link UUID} identifiers.
	 * @return the {@link Function} for such conversion.
	 */
	def toUuidFunction[T <: UniquelyIdentifiable]: Function[T, UUID] = {
		new Function[T, UUID] {
			override def apply(input: T) = input.getIdentifier
		};
	}
	
	/**
	 * Gets a function for converting {@link UniquelyIdentifiable} instances to normalized {@link String}
	 * representations of their {@link UUID} identifiers.
	 * @return the {@link Function} for such conversion.
	 */
	def toUuidStringFunction[T <: UniquelyIdentifiable]: Function[T, String] = {
		new Function[T, String] {
			override def apply(input: T) = UuidUUID(Option(input.getIdentifier)).toString
		};
	} 
	
	/**
	 * Gets a predicate for testing equality of {@link UniquelyIdentifiable} instances with a given identifier.
	 * @param identifier the {@link UUID} to test against.
	 * @return a {@link Predicate} that can be used for this test.
	 */
	def identifierEqualsPredicate[T <: UniquelyIdentifiable](identifier: UUID): Predicate[T] = {
		new Predicate[T] {
			override def apply(input: T) = identifier equals input.getIdentifier
		};
	}

	/**
	 * Checks to see if a given string is a valid UUID or not. Ignores the dashes.
	 * @param uuidString the string containing the UUID.
	 * @return {@code true} if the passed string is a valid UUID, {@code false} otherwise.
	 */
	def isUuid(uuid: String) = UuidString(Option(uuid)).isUuid
	
	/**
	 * Create UUID from a byte array.
	 * @param uuidBytes Byte representation of the UUID.
	 * @return The UUID object.
	 */
	def create(uuid: Array[Byte]) = UuidBytes(Option(uuid)).toUUID
	
	/**
	 * Creates a {@link UUID} from any valid UUID string.
	 * @param uuidString the string containing the UUID.
	 * @return a {@link UUID} object if the provided string is a valid UUID.
	 * @throws IllegalArgumentException when the provided string is not a valid UUID representation.
	 */
	def create(uuid: String) = UuidString(Option(uuid)).toUUID
	
	/**
	 * Converts the UUID to its {@code byte} array equivalent.
	 * @param uuid The {@link UUID} to convert to bytes.
	 * @return The byte array representing the UUID.
	 */
	def toBytes(uuid: UUID) = UuidUUID(Option(uuid)).toBytes
	
	/**
	 * Converts the UUID to its {@code byte} array equivalent.
	 * @param uuidString the {@link String} representation of the UUID.
	 * @return the byte array representing the UUID.
	 */
	def toBytes(uuid: String) = UuidString(Option(uuid)).toBytes
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuidString the UUID {@link String} to normalize.
	 * @return the normalized UUID string.
	 */
	def normalize(uuid: String) = UuidString(Option(uuid)).toString
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuid the {@link UUID} to normalize.
	 * @return the normalized UUID string.
	 */
	def normalize(uuid: UUID) = UuidUUID(Option(uuid)).toString
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuid the {@link Byte} array UUID to normalize.
	 * @return the normalized UUID string.
	 */
	def normalize(uuid: Array[Byte]) = UuidBytes(Option(uuid)).toString
}