/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v3_1.ast

import org.neo4j.cypher.internal.frontend.v3_1.test_helpers.CypherFunSuite

class QueryTaggerTest extends CypherFunSuite {

  test(queryTag(MatchTag)) {
    QueryTagger("MATCH n RETURN n") should contain(MatchTag)
  }

  test(queryTag(RegularMatchTag)) {
    QueryTagger("MATCH n RETURN n") should contain(RegularMatchTag)
  }

  test(queryTag(OptionalMatchTag)) {
    QueryTagger("OPTIONAL MATCH n RETURN 1") should contain(OptionalMatchTag)
  }

  test(queryTag(ShortestPathTag)) {
    QueryTagger("MATCH shortestPath( ()-[*]->() ) RETURN 1") should contain(ShortestPathTag)
  }

  test(queryTag(NamedPathTag)) {
    QueryTagger("MATCH p = ()-[*]->() RETURN 1") should contain(NamedPathTag)
  }

  test(queryTag(SingleLengthRelTag)) {
    QueryTagger("MATCH ()-[]->() RETURN 1") should contain(SingleLengthRelTag)
  }

  test(queryTag(VarLengthRelTag)) {
    QueryTagger("MATCH ()-[*]->() RETURN 1") should contain(VarLengthRelTag)
  }

  test(queryTag(DirectedRelTag)) {
    QueryTagger("MATCH ()-[]->() RETURN 1") should contain(DirectedRelTag)
  }

  test(queryTag(UnDirectedRelTag)) {
    QueryTagger("MATCH ()-[]-() RETURN 1") should contain(UnDirectedRelTag)
  }

  test(queryTag(RelPatternTag)) {
    QueryTagger("MATCH ()-[]->() RETURN 1") should contain(RelPatternTag)
  }

  test(queryTag(SingleNodePatternTag)) {
    QueryTagger("MATCH () RETURN 1") should contain(SingleNodePatternTag)
    QueryTagger("MATCH ()-[]->() RETURN 1") should not contain SingleNodePatternTag
  }

  test(queryTag(VariableExpressionTag)) {
    QueryTagger("RETURN n") should contain(VariableExpressionTag)
  }

  test(queryTag(LiteralExpressionTag)) {
    QueryTagger("RETURN 1") should contain(LiteralExpressionTag)
  }

  test(queryTag(ParameterExpressionTag)) {
    QueryTagger("RETURN {param}") should contain(ParameterExpressionTag)
  }

  test(queryTag(ComplexExpressionTag)) {
    QueryTagger("RETURN n + 1") should contain(ComplexExpressionTag)
    QueryTagger("RETURN {param}") should contain(ComplexExpressionTag)
    QueryTagger("RETURN n") should not contain ComplexExpressionTag
    QueryTagger("RETURN 1") should not contain ComplexExpressionTag
  }

  test(queryTag(FilteringExpressionTag)) {
    QueryTagger("RETURN any(n in [1,2] where n > 0)") should contain(FilteringExpressionTag)
  }

  test(queryTag(WhereTag)) {
    QueryTagger("MATCH n WHERE n.prop RETURN n") should contain(WhereTag)
  }

  test(queryTag(WithTag)) {
    QueryTagger("MATCH n WITH n.prop AS x RETURN 1") should contain(WithTag)
  }

  test(queryTag(ReturnTag)) {
    QueryTagger("MATCH n RETURN n.prop AS x") should contain(ReturnTag)
  }

  test(queryTag(StartTag)) {
    QueryTagger("START n=node(0) RETURN id(n)") should contain(StartTag)
  }

  test(queryTag(UnionTag)) {
    QueryTagger("MATCH n UNION MATCH n RETURN n") should contain(UnionTag)
  }

  test(queryTag(UnwindTag)) {
    QueryTagger("UNWIND [1, 2] AS x RETURN x") should contain(UnwindTag)
  }

  test(queryTag(LoadCSVTag)) {
    QueryTagger("LOAD CSV WITH HEADERS FROM \"http://somewhere/file.csv\" AS csvLine\nCREATE (p:Person { id: toInt(csvLine.id), name: csvLine.name })") should contain(LoadCSVTag)
  }

  test(queryTag(UpdatesTag)) {
    QueryTagger("CREATE ()") should contain(UpdatesTag)
    QueryTagger("CREATE ()-[]->()") should contain(UpdatesTag)
    QueryTagger("CREATE UNIQUE ()-[]->()") should contain(UpdatesTag)
    QueryTagger("MERGE (n:X {id: 12})") should contain(UpdatesTag)
    QueryTagger("MATCH a, b MERGE (a)-[n:X {id: 12}]->(b)") should contain(UpdatesTag)
    QueryTagger("MATCH n REMOVE n.foo") should contain(UpdatesTag)
    QueryTagger("MATCH n SET n.foo = 12") should contain(UpdatesTag)
    QueryTagger("MATCH n DELETE n") should contain(UpdatesTag)
  }

  test(queryTag(CreateTag)) {
    QueryTagger("CREATE ()") should contain(CreateTag)
    QueryTagger("CREATE ()-[:T]->()") should contain(CreateTag)
  }

  test(queryTag(DeleteTag)) {
    QueryTagger("CREATE (n) DELETE n") should contain(DeleteTag)
    QueryTagger("CREATE ()-[r:T]->() DELETE r") should contain(DeleteTag)
  }

  test(queryTag(SetTag)) {
    QueryTagger("CREATE (n) SET n:L") should contain(SetTag)
    QueryTagger("CREATE (n) SET n.prop = 0") should contain(SetTag)
  }

  test(queryTag(RemoveTag)) {
    QueryTagger("CREATE (n) REMOVE n:L") should contain(RemoveTag)
    QueryTagger("CREATE (n) REMOVE n.prop") should contain(RemoveTag)
  }

  test(queryTag(MergeTag)) {
    QueryTagger("MERGE ()") should contain(MergeTag)
    QueryTagger("MERGE ()-[r:T]->()") should contain(MergeTag)
  }

  test(queryTag(CreateUniqueTag)) {
    QueryTagger("CREATE UNIQUE ()") should contain(CreateUniqueTag)
    QueryTagger("CREATE UNIQUE ()-[r:T]->()") should contain(CreateUniqueTag)
  }

  test(queryTag(ForeachTag)) {
    QueryTagger("FOREACH (i IN [1,2,3] | CREATE ())") should contain(ForeachTag)
  }

  test("Supports combining tags") {
    QueryTagger("MATCH n RETURN n") should be(Set(
      MatchTag,
      RegularMatchTag,
      SingleNodePatternTag,
      ReturnTag,
      VariableExpressionTag)
    )
  }

  private def queryTag(tag: QueryTag) = tag.toString
}
