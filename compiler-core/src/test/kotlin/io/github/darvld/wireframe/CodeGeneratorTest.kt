package io.github.darvld.wireframe

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class CodeGeneratorTest {
    @Test
    fun `test generation`() {
        val sources = listOf(WireframeCompiler.Source(
            sdl = SampleSchema,
            packageName = "io.github.darvld.wireframe.samples",
        ))

        val generator = WireframeCompiler()
        val output = generator.process(
            project = "Test",
            basePackage = "com.example.test",
            sources = sources,
        )

        output.forEach {
            println("====================================================")
            println("= File: ${it.name}.kt")
            println("====================================================")

            it.writeTo(System.out)
        }

        println("Done")
    }

    private companion object {
        @Language("GraphQL")
        @Suppress("GraphQLUnresolvedReference")
        const val SampleSchema = """
            interface Media {
                url: String!
            }
            
            type Image implements Media {
                url: String!
                width: Int!
                height: Int!
            }
            
            "A sample enum."
            enum Status {
                On,
                Off
            }
            
            "A message sent by a user."
            type Message {
                author: String!
                content: String!
            }
            
            extend type User {
                "The age of the user"
                age: Int!
                
                surname: String
            }
            
            extend type User {
                dni: String
            }
            
            extend type Query {
                chat(id: ID!): Chat!
            }
            
            "Message input structure"
            input MessageInput {
                "The content of the message"
                content: String!
            }
            
            type User {
                name: String!
            }
            
            type Chat {
                id: ID!
                title: String!
                history: [Message!]!
                users: [User!]!
            }
            
            type Query {
                "The chat history for the room with the given id."
                chatHistory(chat: ID!): [Message!]!
            }
            
            type Mutation {
                sendMessage(author: ID!, message: MessageInput): Boolean
            }
        """
    }
}