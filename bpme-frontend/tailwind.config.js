

/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ["class"],
  content: ["./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#FF7869",
        secondary: "#A3FFD0",
        black: "#303030",
        "black-75": "#2A2221",
        "black-50": "#949090",
        "black-25": "#C9C7C7",
        "grey-100": "#B0B0B0",
        "grey-75": "#D1D1D1",
        "grey-50": "#DEDEDE",
        "grey-25": "#E9E9E9",
      },
    },

    fontFamily: {
      sans: ["Libre Franklin", "Manrope"],
    },
    fontSize: {
      // subtracted 3px from each
      h1: "72px",
      h2: "53px",
      h3: "39px",
      h4: "29px",
      h5: "21px",
      h6: "18px",
    },

  },

  plugins: [require("tailwindcss-animate")],
};
