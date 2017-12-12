# Acropolis Software Shared: Scheduler

A small utility package that wraps around the system task scheduler.

Two implementations are provided:
- On Windows this is a wrapper on top of the schtasks utility;
- On Unix or MacOSX, this is a wrapper on top of crontab.

This utility can create scheduled tasks with various simple schedule, and can list the tasks it created. However the scheduling details can't be retrieved back.
