#!/bin/bash

# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

generate_for_dpi() {
    inkscape -o "launcher_icon_background-$1.png" --export-area=15:15:93:93 -w "$2" -h "$2" launcher_icon_background.svg
    inkscape -o "launcher_icon_foreground-$1.png" --export-area=15:15:93:93 -w "$2" -h "$2" launcher_icon_foreground.svg
    convert "launcher_icon_background-$1.png" "launcher_icon_foreground-$1.png" -composite "launcher_icon-$1.png"
    rm "launcher_icon_background-$1.png" "launcher_icon_foreground-$1.png"
}
generate_for_dpi mdpi 48
generate_for_dpi hdpi 72
generate_for_dpi xhdpi 96
generate_for_dpi xxhdpi 144
generate_for_dpi xxxhdpi 192
inkscape -o launcher_icon-play.png --export-area=18:18:90:90 -w 512 -h 512 -b white launcher_icon_foreground.svg
