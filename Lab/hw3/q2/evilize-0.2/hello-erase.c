/* Copyright (C) 2006 Peter Selinger. This file is distributed under
   the terms of the GNU General Public License. See the file COPYING
   for details. */

#include <stdio.h>
#include <unistd.h>

/* do something innocent */
int main_good(int ac, char *av[]) {
  char buf[10];
  printf("Hello world!!!!");
  return 0;
}

/* do something evil */
int main_evil(int ac, char *av[]) {
  printf("it's a virus :)))");
  return 0;
}
