(when-not (running-in-repl?)
  (change-defaults :emitter     'midje.emission.plugins.junit
                   :print-level :print-facts
                   :colorize    false))
