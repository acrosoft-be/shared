@for /l %%x in (1, 1, %2) do @echo %3 && @echo %3 1>&2
@echo before pause
@ping 127.0.0.1 -n %4 > NUL
@echo after pause
@exit /B %1