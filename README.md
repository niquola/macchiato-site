## HealthSamurai Official Site

[![Build Status](https://travis-ci.com/HealthSamurai/aidbox-site.svg?token=BDkPmPwZvvYHsy2jdxs9&branch=master)](https://travis-ci.com/HealthSamurai/aidbox-site)



[Site link](http://healthsamurai.github.io/aidbox-site)

```sh
# dev server
lein start

# generate
lein generate
```


## Run with auto reload

```sh
lein ring server

// use other port
lein ring server 4000
```


# Troubleshooting

```sh
git submodule init
git submodule update
```
If does't work, you need delete vendr/esthatic folder and run command again.
