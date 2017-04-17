#include "gme/Music_Emu.h"
#include <stdlib.h>
#include <stdio.h>
#include "string.h"
#include "br_com_zuriquebolos_songsofclassicgames_GMEPlayer.h"

Music_Emu* emu;
bool carregado = false;
const char* ultimoErro;

JNIEXPORT jboolean JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_carregarArquivo
  (JNIEnv * env, jobject obj, jstring nomeArquivo, jint taxaAmostra) {

	if(carregado) {
		gme_delete(emu);
		carregado = false;
	}
	const char* nomeArquivoTemp = env->GetStringUTFChars(nomeArquivo, NULL);
	long taxaAmostraTemp = (long) taxaAmostra;

	ultimoErro = gme_open_file(nomeArquivoTemp, &emu, taxaAmostraTemp);
	if(ultimoErro) return false;

	ultimoErro = gme_start_track(emu, 0);
	if(ultimoErro) return false;

	carregado = true;
	return carregado;
}

JNIEXPORT jboolean JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_carregarDados
  (JNIEnv * env, jobject obj, jbyteArray dados, jint taxaAmostra) {

	if(carregado) {
		gme_delete(emu);
		carregado = false;
	}
	jbyte* dadosTemp = env->GetByteArrayElements(dados, NULL);
	long taxaAmostraTemp = (long) taxaAmostra;

	ultimoErro = gme_open_data(dadosTemp, env->GetArrayLength(dados), &emu, taxaAmostraTemp);
	if(ultimoErro) return false;

	ultimoErro = gme_start_track(emu, 0);
	if(ultimoErro) return false;

	env->ReleaseByteArrayElements(dados, dadosTemp, 0);
	carregado = true;
	return carregado;
}

JNIEXPORT jshortArray JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_obterAmostra
  (JNIEnv * env, jobject obj, jlong tamanhoAmostra) {

	short amostraTemp[tamanhoAmostra];
	jshortArray amostra = env->NewShortArray(tamanhoAmostra);
	gme_play(emu, tamanhoAmostra, amostraTemp);
	env->SetShortArrayRegion(amostra, 0, tamanhoAmostra, amostraTemp);
	return amostra;
}

JNIEXPORT jboolean JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_terminouMusica
  (JNIEnv * env, jobject obj) {

	return gme_track_ended(emu);
}

JNIEXPORT jstring JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_obterUltimoErro
  (JNIEnv * env, jobject obj) {

	return env->NewStringUTF(ultimoErro);
}

JNIEXPORT void JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_configurarDiminuicaoVolume
  (JNIEnv * env, jobject obj, jlong milisegundosInicio, jlong milisegundosDuracao) {

	emu->set_fade(milisegundosInicio, milisegundosDuracao);
}

JNIEXPORT jobjectArray JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_obterInformacoesMusica
  (JNIEnv * env, jobject obj) {

	track_info_t informacoesMusica;
	ultimoErro = gme_track_info(emu, &informacoesMusica, 0);
	if(ultimoErro) return NULL;

	jobjectArray informacoesMusicaTemp = env->NewObjectArray(10, env->FindClass("java/lang/String"), NULL);

	char informacaoMusica[256];
	sprintf(informacaoMusica, "%d", informacoesMusica.length);
	env->SetObjectArrayElement(informacoesMusicaTemp, 0, env->NewStringUTF(informacaoMusica));
	sprintf(informacaoMusica, "%d", informacoesMusica.intro_length);
	env->SetObjectArrayElement(informacoesMusicaTemp, 1, env->NewStringUTF(informacaoMusica));
	sprintf(informacaoMusica, "%d", informacoesMusica.loop_length);
	env->SetObjectArrayElement(informacoesMusicaTemp, 2, env->NewStringUTF(informacaoMusica));

	env->SetObjectArrayElement(informacoesMusicaTemp, 3, env->NewStringUTF(informacoesMusica.song));
	env->SetObjectArrayElement(informacoesMusicaTemp, 4, env->NewStringUTF(informacoesMusica.game));
	env->SetObjectArrayElement(informacoesMusicaTemp, 5, env->NewStringUTF(informacoesMusica.system));
	env->SetObjectArrayElement(informacoesMusicaTemp, 6, env->NewStringUTF(informacoesMusica.author));
	env->SetObjectArrayElement(informacoesMusicaTemp, 7, env->NewStringUTF(informacoesMusica.copyright));
	env->SetObjectArrayElement(informacoesMusicaTemp, 8, env->NewStringUTF(informacoesMusica.comment));
	env->SetObjectArrayElement(informacoesMusicaTemp, 9, env->NewStringUTF(informacoesMusica.dumper));

	return informacoesMusicaTemp;
}

JNIEXPORT void JNICALL Java_br_com_zuriquebolos_songsofclassicgames_GMEPlayer_liberarRecursos
  (JNIEnv * env, jobject obj) {

	gme_delete(emu);
	carregado = false;
}
