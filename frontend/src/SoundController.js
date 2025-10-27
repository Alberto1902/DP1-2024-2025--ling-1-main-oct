import React, { useEffect, useState } from "react";
import tokenService from "./services/token.service";
import { useLocation } from "react-router-dom";

function SoundController() {
  const jwt = tokenService.getLocalAccessToken();
  const [showVolumeSlider, setShowVolumeSlider] = useState(false);
  const [volume, setVolume] = useState(0.3);
  const [audioEnabled, setAudioEnabled] = useState(false);
  const [currentAudio, setCurrentAudio] = useState(null);
  const [clickCount, setClickCount] = useState(0);
  const [easterEggText, setEasterEggText] = useState("");
  const [backgroundAudio, setBackgroundAudio] = useState(null);
  const [isSequenceCompleted, setIsSequenceCompleted] = useState(
    localStorage.getItem("sequenceCompleted") === "true"
  );
  const [timeoutId, setTimeoutId] = useState(null);
  const location = useLocation();

  const audioSubtitlePairs = [
    { audio: require("./static/audios/random1.mp3"), subtitle: "Freedom is not given, it’s taken. Will you dare to claim it?" },
    { audio: require("./static/audios/random2.mp3"), subtitle: "Every great escape begins with a single step… and a mind unchained." },
    { audio: require("./static/audios/random3.mp3"), subtitle: "Elba is not just a prison—it’s a mirror. What will you see in your reflection?" },
    { audio: require("./static/audios/random4.mp3"), subtitle: "A prisoner with a plan is no prisoner at all." },
    { audio: require("./static/audios/random5.mp3"), subtitle: "Even in exile, destiny can be rewritten." },
    { audio: require("./static/audios/random6.mp3"), subtitle: "This island is your cage, but perhaps also your salvation." },
    { audio: require("./static/audios/random7.mp3"), subtitle: "Freedom lies not in where you are, but in what you do." },
    { audio: require("./static/audios/random8.mp3"), subtitle: "The journey begins not with your feet, but with your decision to move." },
    { audio: require("./static/audios/random9.mp3"), subtitle: "Are you the real Napoleon? Or just a pretender trying to fill his boots?" },
    { audio: require("./static/audios/random10.mp3"), subtitle: "Welcome back. Like a gambler with a losing streak, you couldn’t stay away, could you?" },
    { audio: require("./static/audios/random11.mp3"), subtitle: "Hey looser, back on Elba? Don’t worry, Napoleon couldn’t escape either." },
    { audio: require("./static/audios/random12.mp3"), subtitle: "Remember that Escaping Elba would take you 10000 years." },
    { audio: require("./static/audios/random13.mp3"), subtitle: "Even Napoleon had excuses for his losses. What’s yours?" },
    { audio: require('./static/audios/random14.mp3'), subtitle: "Escape plan #42: dig a tunnel with a spoon. Foolproof!" },

  ];

  const narrationPairs = [
    { audio: require("./static/audios/first0.mp3"), text: "Ah, there you are! I was starting to think you got lost." },
    { audio: require("./static/audios/first001.mp3"), text: "We’re going to have so much fun together—well, at least I will!" },
    { audio: require("./static/audios/first01.mp3"), text: "Oh, muting me already? Well, that’s one way to make this less fun. Your loss!" },
    { audio: require("./static/audios/first1.mp3"), text: "Haha! You really thought you could get over me?!" },
    { audio: require("./static/audios/first2.mp3"), text: "Oh no, still here? Maybe refreshing the page helps... or does it?" },
    { audio: require("./static/audios/first3.mp3"), text: "Guess what? I’m like glitter—you’ll never get rid of me." },
    { audio: require("./static/audios/first4.mp3"), text: "Persistence is key, they say. Let’s see who’s more persistent—me or you!" },
    { audio: require("./static/audios/first5.mp3"), text: "Aww, still trying? This is adorable! Keep going, champ." },
    { audio: require("./static/audios/first6.mp3"), text: "Alright, fine, I’ll leave… or maybe not. Just kidding. See you soon!" },
  ];

  const playAudio = (audio, subtitle) => {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
    }
    setEasterEggText(subtitle);
    const audioElement = new Audio(audio);
    setCurrentAudio(audioElement);
    audioElement.play();
    audioElement.onended = () => {
      setTimeout(() => {
        setEasterEggText("");
      }, 1000);
      setCurrentAudio(null);
    };
  };

  const handleVolumeChange = (e) => {
    const newVolume = parseFloat(e.target.value);
    setVolume(newVolume);
    if (backgroundAudio) {
      backgroundAudio.volume = newVolume * 0.3;
    }
  };

  const toggleAudio = () => {
    if (isSequenceCompleted || !location.pathname === '/') {
      if (!audioEnabled) {
        setAudioEnabled(true);
        if (location.pathname === '/' && jwt) {
          const { audio, subtitle } =
            audioSubtitlePairs[Math.floor(Math.random() * audioSubtitlePairs.length)];
          setTimeout(() => {
            playAudio(audio, subtitle);
          }, 1000);
        }
      } else {
        setAudioEnabled(false);
        if (currentAudio) {
          currentAudio.pause();
          currentAudio.currentTime = 0;
        }
        setEasterEggText("");
      }
      return;
    }
    const newCount = clickCount + 2;
    if (newCount > 1 && newCount <= narrationPairs.length + 1) {
      if (!audioEnabled) setAudioEnabled(true);
      const narrIndex = newCount - 2;
      const { audio, text } = narrationPairs[narrIndex];
      playAudio(audio, text);
      if (newCount === narrationPairs.length + 1) {
        localStorage.setItem("sequenceCompleted", "true");
        setIsSequenceCompleted(true);
      }
    }
    setClickCount(newCount);
  };


  const handleMouseEnter = () => {
    if (timeoutId) clearTimeout(timeoutId);
    setShowVolumeSlider(true);
  };

  const handleMouseLeave = () => {
    const id = setTimeout(() => {
      setShowVolumeSlider(false);
    }, 2000);
    setTimeoutId(id);
  };

  useEffect(() => {
    let bgAudio = new Audio(require("./static/audios/background2.mp3"));
    if (jwt) {
      bgAudio = new Audio(require("./static/audios/background1.mp3"));
    }
    bgAudio.loop = true;
    setBackgroundAudio(bgAudio);
    return () => {
      bgAudio.pause();
      bgAudio.src = "";
    };
  }, []);

  useEffect(() => {
    if (backgroundAudio) {
      if (audioEnabled) {
        backgroundAudio.volume = volume;
        backgroundAudio.play().catch(() => { });
      } else {
        backgroundAudio.volume = 0;
      }
    }
  }, [audioEnabled, backgroundAudio, volume]);

  useEffect(() => {
    if (!jwt) {
      const timer = setTimeout(() => {
        const audio = new Audio(require('./static/audios/eEgg1.mp3'));
        audio.play().catch(error => console.error("Error playing audio:", error));

        const subtitles = [
          { time: 0, text: "Really?! You waited two minutes for this?" },
          { time: 3000, text: "Well, welcome to this little hidden corner of Elba." },
          { time: 6000, text: "Anyway, let’s get real: do you have a plan to escape," },
          { time: 10000, text: "or are you just winging it?" },
          { time: 11500, text: "Because if your plan involves throwing coconuts at Campbell…" },
          { time: 14500, text: "Well, not the worst idea, but you could at least do it with some flair." },
          { time: 19000, text: "Good luck, you will need it!" }
        ];

        subtitles.forEach((subtitle, index) => {
          setTimeout(() => {
            setEasterEggText(subtitle.text);
          }, subtitle.time);
        });

        setTimeout(() => {
          setEasterEggText('');
        }, subtitles[subtitles.length - 1].time + 4000);
      }, 120000);

      return () => clearTimeout(timer);
    }
  }, []);

  return (
    <>
      {easterEggText && (
        <div className="easter-egg-text">
          {easterEggText}
        </div>
      )}
      <div
        data-testid="sound-controller-container"

        style={{
          position: "fixed",
          bottom: "2.5vh",
          right: "3vh",
          zIndex: 1000,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        <button
          onClick={toggleAudio}
          style={{
            backgroundColor: "rgba(0, 0, 0, 0.7)",
            color: "white",
            border: "none",
            borderRadius: "50%",
            width: "6.5vh",
            height: "6.5vh",
            cursor: "pointer",
            position: "relative",
          }}
        >
          {audioEnabled ? "🔊" : "🔇"}
        </button>
        <div
          style={{
            display: "flex",
            flexDirection: "column", // Organiza los botones verticalmente
            alignItems: "center", // Centra horizontalmente
            justifyContent: "center", // Centra verticalmente
            position: "absolute",
            bottom: "calc(100% + 10px)", // Coloca el contenedor encima del botón
            right: "50%", // Centra el contenedor horizontalmente
            transform: "translateX(50%)", // Ajusta el centrado
            transformOrigin: "center center",
            transition: "transform 0.4s cubic-bezier(0.68, -0.55, 0.27, 1.55), opacity 0.4s ease",
            opacity: showVolumeSlider ? 1 : 0,
            backgroundColor: "rgba(0, 0, 0, 0.8)",
            borderRadius: "15px", // Reduce el radio para hacerlo más pequeño
            padding: "5px", // Reduce el padding
            boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
            overflow: "hidden",
            width: "40px", // Ancho reducido
          }}
        >
          {showVolumeSlider && (
            <>
              <button
                onClick={() => setVolume(Math.min(volume + 0.1, 1))} 
                style={{
                  backgroundColor: "transparent",
                  border: "none",
                  color: "white",
                  fontSize: "14px",
                  cursor: "pointer",
                  marginBottom: "3px", 
                }}
              >
                +
              </button>
              <span
                style={{
                  color: "white",
                  fontSize: "12px", 
                  margin: "3px 0",
                }}
              >
                {Math.round(volume * 100)/10}
              </span>
              <button
                onClick={() => setVolume(Math.max(volume - 0.1, 0))} 
                style={{
                  backgroundColor: "transparent",
                  border: "none",
                  color: "white",
                  fontSize: "14px",
                  cursor: "pointer",
                  marginTop: "3px", 
                }}
              >
                -
              </button>
            </>
          )}
        </div>

      </div>
    </>
  );
}

export default SoundController;
