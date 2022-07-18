package io.github.darvld.graphql

import com.squareup.kotlinpoet.FileSpec
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.github.darvld.graphql.analysis.*
import io.github.darvld.graphql.extensions.buildFile
import io.github.darvld.graphql.extensions.isRouteType
import io.github.darvld.graphql.extensions.pack
import io.github.darvld.graphql.generation.buildDecoder
import io.github.darvld.graphql.generation.buildSpec
import io.github.darvld.graphql.model.*
import java.nio.file.Path

/**Analyzes `.graphqls` schema definitions using graphql-java and outputs type-safe Kotlin code for the types and
 *  operations in the schema.*/
public class GraphQLCodeGenerator {

    /**Encapsulates an output element from the code generator. Use it to write the generated code to an [Appendable],
     * or as a file in a target directory.*/
    @JvmInline
    public value class Output internal constructor(private val spec: FileSpec) {
        public val name: String
            get() = spec.name

        public val packageName: String
            get() = spec.packageName

        public fun writeTo(directory: Path) {
            spec.writeTo(directory)
        }

        public fun writeTo(out: Appendable) {
            spec.writeTo(out)
        }
    }

    /**Analyzes the provided GraphQL schema sources and collects necessary data for code generation in the target package.*/
    public fun analyze(packageName: String, sources: List<String>): GenerationEnvironment {
        val parser = SchemaParser()

        // Parse all sources and merge declarations into a single registry
        val registry: TypeDefinitionRegistry = sources.fold(TypeDefinitionRegistry()) { current, next ->
            current.merge(parser.parse(next))
        }

        // Create a schema with an empty wiring, this allows us to analyze the schema generated by graphql-java
        val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(
            /* typeRegistry = */ registry,
            /* wiring = */ RuntimeWiring.newRuntimeWiring().build()
        )

        val inputTypes = mutableSetOf<InputDTO>()
        val outputTypes = mutableSetOf<OutputDTO>()
        val enumTypes = mutableSetOf<EnumDTO>()
        val routes = mutableSetOf<RouteData>()

        for (type: GraphQLNamedType in schema.allTypesAsList) {
            if (type.ignoreForAnalysis()) continue

            if (type.isRouteType()) {
                routes.addAll(processRouteType(type))
                continue
            }

            if (type is GraphQLEnumType) {
                enumTypes.add(processEnumType(type, packageName))
                continue
            }

            if (type is GraphQLObjectType) {
                outputTypes.add(processOutputType(type, packageName))
                routes.addAll(processAdditionalRoutes(type))
                continue
            }

            if (type is GraphQLInputObjectType) {
                inputTypes.add(processInputType(type, packageName))
                continue
            }
        }

        return GenerationEnvironment(
            packageName,
            inputTypes,
            outputTypes,
            enumTypes,
            routes,
        )
    }

    /**Generate type-safe Kotlin DTOs and mappers for input and output types, and route handler extensions for queries,
     *  mutations and subscriptions.
     *
     * A pseudo-constructor will be generated for each input type, taking a `Map<String, Any?>` as its only parameter,
     * to enable conversion from graphql-java's map-based input schemas.
     *
     * The returned sequence triggers the actual code generation when collected.*/
    public fun generate(environment: GenerationEnvironment): Sequence<Output> = with(environment) {
        return sequence {
            for (inputDTO in inputTypes) yield(buildFile(inputDTO.name) {
                addType(inputDTO.buildSpec(environment))
                addFunction(inputDTO.buildDecoder(environment))
            })

            for ((routeKind, routes) in routeHandlers.groupBy(RouteData::operation)) {
                yield(buildFile(routeKind.outputFileName) {
                    for (route in routes) addFunction(route.buildSpec(environment))
                })
            }

            outputTypes.forEach { yield(it.buildSpec(environment).pack(packageName)) }
            enumTypes.forEach { yield(it.buildSpec().pack(packageName)) }
        }.map(::Output)
    }
}