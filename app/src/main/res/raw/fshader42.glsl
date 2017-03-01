precision mediump float;

uniform sampler2D s_texture;

varying vec2 v_texCoord;
varying vec4 v_Color;

void main() {
	gl_FragColor = texture2D( s_texture, v_texCoord ) * v_Color;
}