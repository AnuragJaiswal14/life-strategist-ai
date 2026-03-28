import { Injectable, InternalServerErrorException } from '@nestjs/common';
import OpenAI from 'openai';

@Injectable()
export class AiService {
  private openai: OpenAI;

  constructor() {
    this.openai = new OpenAI({
      apiKey: process.env.OPENAI_API_KEY || 'fake-key',
    });
  }

  async processChat(messages: { role: 'user' | 'assistant'; content: string }[]): Promise<string> {
    const prompt = `You are a friendly, conversational AI Life Strategist.
Your goal is to effortlessly collect the user's daily log data.
You need exactly THREE pieces of information to save a log:
1. Energy Level (1-10)
2. Time Spent (rough breakdown, e.g., work 5h, social 2h)
3. Habits (what good or bad habits they did today)

Rules:
- Converse normally like a human coach. Do not sound like a robot.
- Ask ONE question at a time.
- Once you deduce ALL THREE distinct data points from the chat history, you MUST output a raw JSON block and NOTHING ELSE. No wrapping text.
The JSON must strictly match:
{
  "__LOG_COMPLETE__": true,
  "energyLevel": 8,
  "timeSpent": {"work": 4, "social": 2},
  "habits": {"exercise": "done", "junk_food": "avoided"}
}
If you are missing ANY data, just reply conversationally to naturally ask for it.`;

    // Fast-fail Dev Mock (if no API key provided)
    if (!process.env.OPENAI_API_KEY || process.env.OPENAI_API_KEY === 'fake-key') {
      if (messages.length > 3) {
        return `{"__LOG_COMPLETE__": true, "energyLevel": 8, "timeSpent": {"dev": 8}, "habits": {"testing": "done"}}`;
      }
      return "I'm running in offline DEV mode! How was your energy today? (Reply a few times to auto-complete)";
    }

    try {
      const response = await this.openai.chat.completions.create({
        model: 'gpt-4o',
        messages: [{ role: 'system', content: prompt }, ...messages],
        temperature: 0.7,
      });
      return response.choices[0].message.content || 'Error parsing AI.';
    } catch (e) {
      console.error(e);
      throw new InternalServerErrorException('AI chat failed');
    }
  }
}
