package io.github.highright1234.shotokonoko.loader

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.resolution.DependencyResolutionException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KProperty

object DynamicLoader {

    private val repository: RepositorySystem by LibraryLoaderField
    private val session: DefaultRepositorySystemSession by LibraryLoaderField

    fun load(repositoriesList: List<Pair<String, String>>, dependenciesList: List<String>) {
        // 대충 LibraryLoader 코드 코틀린형식으로 만듦
        if (repositoriesList.isEmpty() || dependenciesList.isEmpty()) return
        val logger = plugin.logger
        logger.info("Loading ${dependenciesList.size} libraries... please wait")

        val dependencies = dependenciesList
            .map { DefaultArtifact(it) }
            .map { Dependency(it, null) }

        val repositories = repositoriesList
            .toMap()
            .map { (id, url) ->
                RemoteRepository.Builder(id, "default", url).build()
            }
            .let { repository.newResolutionRepositories(session, it) }


        try {
            repository.resolveDependencies(
                session,
                DependencyRequest(CollectRequest(null as Dependency?, dependencies, repositories), null)
            )
        } catch (ex: DependencyResolutionException) {
            throw RuntimeException("Error resolving libraries", ex)
        }.artifactResults
            .asSequence()
            .map { it.artifact.file }
            .map {
                try {
                    it.toURI().toURL()
                } catch (ex: MalformedURLException) {
                    throw AssertionError(ex)
                }
            }.onEach {
                logger.info("Loaded library ${it.file}")
            }.forEach {
                loadLibrary(it)
            }
    }

    // I can't understand this code
    // cuz I got this code in stackoverflow
    private fun loadLibrary(url: URL) {
        val classLoader = javaClass.classLoader
        URLClassLoader::class
            .java.getDeclaredMethod("addURL", URL::class.java)
            .apply { isAccessible = true }
            .invoke(classLoader, url)
    }

    private object LibraryLoaderField {
        private val pluginLoader = plugin.pluginLoader as JavaPluginLoader
        private val libraryLoader
        get() = JavaPluginLoader::class.java.getDeclaredField("libraryLoader").apply {
            isAccessible = true
        }[pluginLoader]

        operator fun <T> getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): T {
            val clazz = libraryLoader::class.java
            @Suppress("UNCHECKED_CAST")
            return clazz.getDeclaredField(property.name).apply { isAccessible = true }[libraryLoader] as T
        }
    }
}