### OVERVIEW

My goal for Macchiato is to provide a stack modeled on Ring based around the
existing Node ecosystem, and a development environment similar to what's
available for Clojure on the JVM.

### THE STACK

I think it makes sense to embrace the Node ecosystem and leverage the existing
modules whenever possible. For example, Ring style cookies map directly to the
cookies NPM module. Conversely, there are a number of excellent ClojureScript
libraries available as well, such as Timbre, Bidi, and Mount.

I used a Ring inspired model where I created wrappers around Node HTTP request and response objects. This allowed adapting parts of Ring, such as its session store implementation, with minimal changes.
