/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiled_runtime.v3_2.codegen

import org.neo4j.cypher.internal.frontend.v3_2.InternalException

/**
  * Configuration modes for code generation
  */
sealed trait CodeGenMode

/**
  * Produces source code
  */
case object SourceCodeMode extends CodeGenMode

/**
  * Produce byte code directly
  */
case object ByteCodeMode extends CodeGenMode

/**
  * Configuration class for code generation
 *
  * @param mode The mode of code generation
  * @param showSource if `true` source code is stored and returned
  * @param packageName The name of the v3_2 the produced code should belong to
  */
case class CodeGenConfiguration(mode: CodeGenMode = CodeGenMode.default,
                                showSource: Boolean = false,
                                showByteCode: Boolean = false,
                                packageName: String = "org.neo4j.cypher.internal.compiler.v3_2.generated"
                               )

object CodeGenConfiguration {
  def apply(debugOptions: Set[String]): CodeGenConfiguration = {
    val mode = if(debugOptions.contains("generate_java_source")) SourceCodeMode else ByteCodeMode
    val show_java_source = debugOptions.contains("show_java_source")
    if (show_java_source && mode != SourceCodeMode) {
      throw new InternalException("Can only 'debug=show_java_source' if 'debug=generate_java_source'.")
    }
    val show_bytecode = debugOptions.contains("show_bytecode")
    CodeGenConfiguration(mode, show_java_source, show_bytecode)
  }
}

object CodeGenMode {
  val default = ByteCodeMode
}
