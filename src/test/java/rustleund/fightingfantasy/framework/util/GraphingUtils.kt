@file:Suppress("HttpUrlsUsage")

package rustleund.fightingfantasy.framework.util

import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.Attribute
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Function
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.streams.asSequence

private val COLORS = listOf("#800000", "#469990", "#000075", "#9A6324", "#3cb44b",  "#f58231", "#4363d8", "#000000")

fun main() {
    val pages = pages().map { it.pageNumber() }.use { it.asSequence().toSet() }
    val graph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    val pagesWithSounds = Files.list(Paths.get("/Users/rustlea/Dropbox/nightdragon/sounds"))
        .use { it.asSequence().map { p -> p.fileName.toString().substringBefore(".") }.toSet() }
    val labels = mutableMapOf<String, String>()
    val deadends = mutableSetOf<String>()
    pages.forEach { graph.addVertex(it) }
    val documentBuilderFactory = DocumentBuilderFactory.newInstance()
    documentBuilderFactory.isNamespaceAware = true
    val documentBuilder = documentBuilderFactory.newDocumentBuilder()
    pages().use { stream ->
        stream.forEach { pageFile ->
            val pageNumber = pageFile.pageNumber()
            val fileContents = String(Files.readAllBytes(pageFile))
            val document = documentBuilder.parse(pageFile.toFile())
            document.documentElement.getAttribute("label").let { labels[pageNumber] = it }
            Regex("(http://link:)|(<link )|(<flaggedLink)").findAll(fileContents).forEach { match ->
                val linkToPage = when (match.value) {
                    "http://link:" -> {
                        val startIndex = match.range.first + 12
                        val endIndex = fileContents.indexOf("\"", match.range.first + 1)
                        fileContents.substring(startIndex, endIndex)
                    }

                    "<flaggedLink" -> {
                        val firstQuote = fileContents.indexOf("page=\"", match.range.first) + 5
                        val secondQuote = fileContents.indexOf("\"", firstQuote + 1)
                        fileContents.substring(firstQuote + 1, secondQuote)
                    }

                    else -> {
                        val firstQuote = fileContents.indexOf("\"", match.range.first)
                        val secondQuote = fileContents.indexOf("\"", firstQuote + 1)
                        fileContents.substring(firstQuote + 1, secondQuote)
                    }
                }
                linkToPage.toIntOrNull()?.let {
                    if (!graph.containsVertex(linkToPage)) graph.addVertex(linkToPage)
                    graph.addEdge(pageNumber, linkToPage)
                }
            }
        }
    }

    graph.vertexSet().filter { graph.outDegreeOf(it) == 0 }.forEach {
        deadends += it
        println("End: $it ${labels[it]}")
    }

    val exporter: DOTExporter<String, DefaultEdge> = DOTExporter()
    exporter.setVertexAttributeProvider { v: Any ->
        val map = mutableMapOf<String, Attribute>()
        map["label"] = DefaultAttribute.createAttribute("$v: ${labels[v]}")
        map["fontname"] = "Helvetica".toAttr()
        if (pagesWithSounds.contains(v)) {
            map["style"] = "filled".toAttr()
            map["fillcolor"] = "#ddffdd".toAttr()
        } else if (deadends.contains(v)) {
            map["style"] = "filled".toAttr()
            map["fillcolor"] = "#ffdddd".toAttr()
        }
        map
    }
    exporter.setEdgeAttributeProvider(object : Function<DefaultEdge, Map<String, Attribute>> {
        private var i = 0

        override fun apply(t: DefaultEdge): Map<String, Attribute> {
            val result = mapOf(
                "color" to COLORS[i].toAttr(),
                "arrowhead" to "empty".toAttr(),
                "style" to "bold".toAttr(),
            )
            i = (i + 1) % COLORS.size
            return result
        }
    })
    Files.newBufferedWriter(Paths.get("/Users/rustlea/Desktop/nightdragon.gv")).use { writer ->
        exporter.exportGraph(graph, writer)
    }
}

private fun String.toAttr() = DefaultAttribute.createAttribute(this)

private fun pages() = Files.list(Paths.get("/Users/rustlea/Dropbox/nightdragon/pages")).filter { it.isPage() }

fun Path.pageNumber(): String {
    return this.fileName.toString().substringBefore(".xml")
}

fun Path.isPage(): Boolean {
    return this.pageNumber().matches(Regex("\\d+"))
}