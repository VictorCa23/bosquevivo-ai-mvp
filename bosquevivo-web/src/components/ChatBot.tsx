import { useEffect, useRef, useState } from "react";

type Message = {
  id: string;
  role: "user" | "bot";
  text: string;
  audio?: string; // base64 audio URL
};

type SpeechRecognitionEvent = {
  results: { [key: number]: { [key: number]: { transcript: string } } };
};

declare global {
  interface Window {
    SpeechRecognition: new () => SpeechRecognitionInstance;
    webkitSpeechRecognition: new () => SpeechRecognitionInstance;
  }
}

type SpeechRecognitionInstance = {
  lang: string;
  interimResults: boolean;
  maxAlternatives: number;
  start: () => void;
  stop: () => void;
  onresult: (event: SpeechRecognitionEvent) => void;
  onerror: (event: { error: string }) => void;
  onend: () => void;
};

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8082";
const KOKORO_URL = import.meta.env.VITE_KOKORO_URL ?? "http://localhost:8880";

function getToken(): string | null {
  try {
    const raw = localStorage.getItem("bosquevivo.session");
    if (!raw) return null;
    const parsed = JSON.parse(raw) as { token?: string };
    return parsed.token ?? null;
  } catch {
    return null;
  }
}

async function askChat(question: string): Promise<string> {
  const token = getToken();
  const response = await fetch(`${API_URL}/api/chat`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ question }),
  });
  if (!response.ok) throw new Error("Error al consultar el servidor");
  const data = await response.json() as { answer: string };
  return data.answer;
}

async function textToSpeech(text: string): Promise<string | null> {
  try {
    const response = await fetch(`${KOKORO_URL}/v1/audio/speech`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ model: "kokoro", input: text, voice: "af_heart" }),
    });
    if (!response.ok) return null;
    const blob = await response.blob();
    return URL.createObjectURL(blob);
  } catch {
    return null;
  }
}

export function ChatBot() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      role: "bot",
      text: "Hola, soy el asistente de BosqueVivo. Puedes preguntarme sobre incidentes, reportes y estadísticas. ¿En qué te ayudo?",
    },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [listening, setListening] = useState(false);
  const recognitionRef = useRef<SpeechRecognitionInstance | null>(null);
  const bottomRef = useRef<HTMLDivElement>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  function startListening() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      alert("Tu navegador no soporta reconocimiento de voz. Usa Chrome.");
      return;
    }
    const recognition = new SpeechRecognition();
    recognition.lang = "es-ES";
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onresult = (event: SpeechRecognitionEvent) => {
      const transcript = event.results[0][0].transcript;
      setInput(transcript);
      setListening(false);
    };
    recognition.onerror = () => setListening(false);
    recognition.onend = () => setListening(false);

    recognitionRef.current = recognition;
    recognition.start();
    setListening(true);
  }

  function stopListening() {
    recognitionRef.current?.stop();
    setListening(false);
  }

  async function sendMessage(text: string) {
    if (!text.trim() || loading) return;
    const userMsg: Message = { id: Date.now().toString(), role: "user", text };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setLoading(true);

    try {
      const answer = await askChat(text);
      const audioUrl = await textToSpeech(answer);

      const botMsg: Message = {
        id: (Date.now() + 1).toString(),
        role: "bot",
        text: answer,
        audio: audioUrl ?? undefined,
      };
      setMessages((prev) => [...prev, botMsg]);

      if (audioUrl) {
        if (audioRef.current) {
          audioRef.current.pause();
        }
        const audio = new Audio(audioUrl);
        audioRef.current = audio;
        void audio.play();
      }
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          role: "bot",
          text: "No pude obtener una respuesta. Verifica que el servidor esté corriendo.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") void sendMessage(input);
  }

  return (
    <>
      {/* Floating button */}
      <button
        onClick={() => setOpen((v) => !v)}
        style={{
          position: "fixed",
          bottom: "24px",
          right: "24px",
          width: "56px",
          height: "56px",
          borderRadius: "50%",
          background: "linear-gradient(135deg, #2d6a4f, #52b788)",
          border: "none",
          cursor: "pointer",
          boxShadow: "0 4px 20px rgba(45,106,79,0.5)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontSize: "24px",
          zIndex: 1000,
          transition: "transform 0.2s",
        }}
        title="Asistente IA"
      >
        {open ? "✕" : "🌿"}
      </button>

      {/* Chat panel */}
      {open && (
        <div
          style={{
            position: "fixed",
            bottom: "90px",
            right: "24px",
            width: "360px",
            maxHeight: "520px",
            background: "#fff",
            borderRadius: "16px",
            boxShadow: "0 8px 40px rgba(0,0,0,0.15)",
            display: "flex",
            flexDirection: "column",
            zIndex: 999,
            overflow: "hidden",
            fontFamily: "'Segoe UI', sans-serif",
          }}
        >
          {/* Header */}
          <div
            style={{
              background: "linear-gradient(135deg, #2d6a4f, #52b788)",
              padding: "14px 18px",
              color: "#fff",
            }}
          >
            <div style={{ fontWeight: 700, fontSize: "15px" }}>🌿 Asistente BosqueVivo</div>
            <div style={{ fontSize: "12px", opacity: 0.85 }}>
              Pregunta sobre incidentes y estadísticas
            </div>
          </div>

          {/* Messages */}
          <div
            style={{
              flex: 1,
              overflowY: "auto",
              padding: "12px",
              display: "flex",
              flexDirection: "column",
              gap: "8px",
              background: "#f8faf9",
            }}
          >
            {messages.map((msg) => (
              <div
                key={msg.id}
                style={{
                  display: "flex",
                  justifyContent: msg.role === "user" ? "flex-end" : "flex-start",
                }}
              >
                <div
                  style={{
                    maxWidth: "80%",
                    padding: "10px 14px",
                    borderRadius:
                      msg.role === "user" ? "16px 16px 4px 16px" : "16px 16px 16px 4px",
                    background: msg.role === "user" ? "#2d6a4f" : "#fff",
                    color: msg.role === "user" ? "#fff" : "#1a1a1a",
                    fontSize: "14px",
                    lineHeight: "1.5",
                    boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
                  }}
                >
                  {msg.text}
                  {msg.audio && (
                    <button
                      onClick={() => {
                        const audio = new Audio(msg.audio);
                        void audio.play();
                      }}
                      style={{
                        display: "block",
                        marginTop: "6px",
                        background: "rgba(255,255,255,0.2)",
                        border: "none",
                        borderRadius: "8px",
                        padding: "3px 8px",
                        fontSize: "12px",
                        cursor: "pointer",
                        color: msg.role === "user" ? "#fff" : "#2d6a4f",
                      }}
                    >
                      🔊 Reproducir
                    </button>
                  )}
                </div>
              </div>
            ))}
            {loading && (
              <div style={{ display: "flex", justifyContent: "flex-start" }}>
                <div
                  style={{
                    padding: "10px 14px",
                    borderRadius: "16px 16px 16px 4px",
                    background: "#fff",
                    fontSize: "14px",
                    color: "#888",
                    boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
                  }}
                >
                  Consultando datos...
                </div>
              </div>
            )}
            <div ref={bottomRef} />
          </div>

          {/* Suggested questions */}
          <div
            style={{
              padding: "8px 12px",
              display: "flex",
              gap: "6px",
              flexWrap: "wrap",
              borderTop: "1px solid #e8f0eb",
              background: "#fff",
            }}
          >
            {[
              "¿Cuántos incidentes hay hoy?",
              "¿Cuántos se cerraron?",
              "¿Cuántos críticos?",
            ].map((q) => (
              <button
                key={q}
                onClick={() => void sendMessage(q)}
                style={{
                  fontSize: "11px",
                  padding: "4px 10px",
                  borderRadius: "20px",
                  border: "1px solid #52b788",
                  background: "#f0faf4",
                  color: "#2d6a4f",
                  cursor: "pointer",
                  whiteSpace: "nowrap",
                }}
              >
                {q}
              </button>
            ))}
          </div>

          {/* Input */}
          <div
            style={{
              padding: "10px 12px",
              display: "flex",
              gap: "8px",
              borderTop: "1px solid #e8f0eb",
              background: "#fff",
            }}
          >
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Escribe o usa el micrófono..."
              disabled={loading}
              style={{
                flex: 1,
                padding: "9px 12px",
                borderRadius: "24px",
                border: "1.5px solid #c8e6d4",
                outline: "none",
                fontSize: "13px",
                background: "#f8faf9",
              }}
            />
            {/* Mic button */}
            <button
              onClick={listening ? stopListening : startListening}
              disabled={loading}
              title={listening ? "Detener" : "Hablar"}
              style={{
                width: "38px",
                height: "38px",
                borderRadius: "50%",
                border: "none",
                background: listening ? "#e63946" : "#e8f5ee",
                cursor: "pointer",
                fontSize: "16px",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                animation: listening ? "pulse 1s infinite" : "none",
              }}
            >
              🎤
            </button>
            {/* Send button */}
            <button
              onClick={() => void sendMessage(input)}
              disabled={loading || !input.trim()}
              style={{
                width: "38px",
                height: "38px",
                borderRadius: "50%",
                border: "none",
                background: input.trim() ? "#2d6a4f" : "#e0e0e0",
                cursor: input.trim() ? "pointer" : "default",
                fontSize: "16px",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: "#fff",
              }}
            >
              ➤
            </button>
          </div>
        </div>
      )}

      <style>{`
        @keyframes pulse {
          0%, 100% { box-shadow: 0 0 0 0 rgba(230,57,70,0.4); }
          50% { box-shadow: 0 0 0 8px rgba(230,57,70,0); }
        }
      `}</style>
    </>
  );
}
