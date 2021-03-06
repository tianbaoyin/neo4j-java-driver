/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.stress;

import java.util.concurrent.atomic.AtomicReference;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.exceptions.ClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BlockingWriteQueryUsingReadSession<C extends AbstractContext> extends AbstractBlockingQuery<C>
{
    public BlockingWriteQueryUsingReadSession( Driver driver, boolean useBookmark )
    {
        super( driver, useBookmark );
    }

    @Override
    public void execute( C context )
    {
        AtomicReference<Result> resultRef = new AtomicReference<>();
        assertThrows( ClientException.class, () ->
        {
            try ( Session session = newSession( AccessMode.READ, context ) )
            {
                Result result = session.run( "CREATE ()" );
                resultRef.set( result );
            }
        } );
        assertNotNull( resultRef.get() );
        assertEquals( 0, resultRef.get().consume().counters().nodesCreated() );
    }
}
