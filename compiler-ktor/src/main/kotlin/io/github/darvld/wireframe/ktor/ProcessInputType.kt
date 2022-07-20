package io.github.darvld.wireframe.ktor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.buildFunction
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.extensions.nullable
import io.github.darvld.wireframe.ktor.InputNames.DecoderMapParameter
import io.github.darvld.wireframe.ktor.extensions.buildFieldExtractor
import io.github.darvld.wireframe.output

private object InputNames {
    const val DecoderMapParameter = "map"
}

internal fun processInputType(type: GraphQLInputObjectType, environment: ProcessingEnvironment) {
    val generatedName = environment.resolve(type)
    val decoder = buildDecoder(generatedName, type, environment)

    environment.output(
        packageName = generatedName.packageName,
        fileName = generatedName.simpleName,
        spec = decoder,
    )
}

internal fun buildDecoder(
    outputType: ClassName,
    definition: GraphQLInputObjectType,
    environment: ProcessingEnvironment,
): FunSpec {
    return buildFunction(outputType.simpleName) {
        markAsGenerated()
        addKdoc("Constructs a new ${outputType.simpleName} from an unsafe map. This is useful for decoding the query parameters provided by graphql-java.")

        returns(outputType)
        addParameter(DecoderMapParameter, MAP.parameterizedBy(STRING, ANY.nullable()))

        addCode(buildCodeBlock {
            add("return·%T(\n", outputType)
            indent()

            for (field in definition.fields) {
                val extractor = environment.buildFieldExtractor(
                    extractor = { CodeBlock.of("%L[%S]·as·%T", DecoderMapParameter, field.name, it) },
                    fieldType = field.type,
                )

                addStatement("%L = %L,", field.name, extractor)
            }

            unindent()
            add(")\n")
        })
    }
}