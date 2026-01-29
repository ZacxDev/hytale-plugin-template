{
  description = "Hytale plugin template - starter project for server mods";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        jdk = pkgs.jdk25;
      in
      {
        devShells.default = pkgs.mkShell {
          name = "hytale-plugin-dev";

          buildInputs = with pkgs; [
            jdk
            gradle
            jq
            curl
            imagemagick
          ];

          shellHook = ''
            export JAVA_HOME="${jdk.home}"
            export GRADLE_USER_HOME="$PWD/.gradle-home"
            echo "Hytale Plugin Dev Environment"
            echo "  Java:   $(java --version 2>&1 | head -1)"
            echo "  Build:  gradle shadowJar"
            echo "  Output: build/libs/ExampleMod-1.0.0.jar"
          '';
        };
      }
    );
}
