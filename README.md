# MPS Interoperability

The goal of this project is to permit interoperability with MPS model from outside MPS. 

## Use cases

* Fix and refactor MPS models outside MPS
* Process MPS models in CI
* Load MPS models in larger pipelines, for example to generate artifacts out of it
* Edit MPS models in external editors, for example web editors
* Perform analysis on MPS models from outside MPS

## Roadmap

* Physical model, testing [DONE]
* Logical model, testing [DOING]
* Loading physical model
* Loading logical model
* Work with heterogeneous API, based on Manifold
* Example with visualization of a model

## Planned features

**Homogeneous API:** API to load MPS models using common classes like Node, Property, Relation, etc. to interact with those models

**Heterogeneous API:** API to work with MPS nodes through classes which are specific for concepts. Those classes could be either generated as source code or as in-memory classes using frameworks like Manifold

## Supported platforms

For now we target the JVM but we could in the future convert to a Kotlin multi-platform project, so that it can be run in the browser and in all the platforms supported by Kotlin/native.

## Export examples

```shell script
../gradlew run --args='--destination /Users/federico/repos/mps-web-editor/build/generated_json /Users/federico/repos/SigiDsl gescomplus.dsl.core.structure'
../gradlew run --args='-i /Users/federico/repos/mpsinterop/mpsinterface/src/test/resources/mps2019_3_1  --destination /Users/federico/repos/mps-web-editor/build/generated_json /Users/federico/repos/SigiDsl all'
```