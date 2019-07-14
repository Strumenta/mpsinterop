# MPS Interoperability

The goal of this project is to permit interoperability with MPS model from outside MPS. 

## Use cases

* Fix and refactor MPS models outside MPS
* Process MPS models in CI
* Load MPS models in larger pipelines, for example to generate artifacts out of it
* Edit MPS models in external editors, for example web editors
* Perform analysis on MPS models from outside MPS

## Planned features

**Homogeneous API:** API to load MPS models using common classes like Node, Property, Relation, etc. to interact with those models

**Heterogeneous API:** API to work with MPS nodes through classes which are specific for concepts. Those classes could be either generated as source code or as in-memory classes using frameworks like Manifold

## Supported platforms

For now we target the JVM but we could in the future convert to a Kotlin multi-platform project, so that it can be run in the browser and in all the platforms supported by Kotlin/native.
