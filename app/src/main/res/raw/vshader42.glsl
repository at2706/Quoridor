uniform mat4 uMVPMatrix;
attribute vec4 a_Position;
attribute vec2 a_texCoord;
uniform vec4 a_Color;

varying vec2 v_texCoord;
varying vec4 v_Color;

void main() {
	gl_Position = uMVPMatrix * a_Position;
	v_texCoord = a_texCoord;
	v_Color = a_Color;
}