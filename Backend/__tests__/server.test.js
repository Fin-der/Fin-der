/**
 * @jest-environment node
 */
const app = require('../server') // Link to your server file
const supertest = require('supertest')
const { MongoClient } = require('mongodb')
const request = supertest(app)


it('gets the test endpoint', async done => {
    const response = await request.get('/test')
  
    expect(response.status).toBe(200)
    expect(response.body.message).toBe('pass!')
    done()
})
/* 
it ('tests onGetAllUsers', async done => {
    const response = await request.get('/users')
  
    expect(response.status).toBe(200)
    done()
}) */