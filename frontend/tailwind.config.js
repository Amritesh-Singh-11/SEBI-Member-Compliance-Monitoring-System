/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        navy: {
          800: '#0F172A',
          900: '#0B0F19',
          950: '#05070D',
        },
        teal: {
          500: '#14B8A6',
          600: '#0D9488',
        },
        slate: {
          850: '#161F32',
        }
      }
    },
  },
  plugins: [],
}
