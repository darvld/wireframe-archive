package io.github.darvld.graphql

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class CodeGeneratorTest {
    @Test
    fun `test generation`() {
        val sources = listOf(SampleSchema)

        val outputDirectory = Path("build/generated/kotlin")
        println("Using directory: $outputDirectory")

        GraphQLCodeGenerator().generate(packageName = "", sources).forEach {
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
            type Message {
                author: String!
                content: String!
            }
            
            input MessageInput {
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
                chatHistory(chat: ID!): [Message!]!
            }
            
            type Mutation {
                sendMessage(author: ID!, message: MessageInput): Boolean
            }
        """
    }
}